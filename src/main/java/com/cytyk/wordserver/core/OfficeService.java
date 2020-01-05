package com.cytyk.wordserver.core;


import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author cytyikai
 * 2019/9/3 13:58
 */
@Service
@Slf4j
public class OfficeService {
    /**
     * 转PDF格式值
     */
    static final int WORD_FORMAT_PDF = 17;

    /**
     * APP数
     */
    private static final Integer APP_NUM = 10;

    /**
     * 当前轮序
     */
    private static volatile Integer CURRENT_INDEX = 0;

    /**
     * 请求数
     */
    private static volatile AtomicInteger REQUEST_COUNT = new AtomicInteger(0);

    /**
     * 默认等待时间 秒
     */
    private final static Integer time = 3;

    /**
     * 计时器
     */
    private static volatile AtomicInteger TIMER = new AtomicInteger(time);

    /**
     * 线程池
     */
    private static ExecutorService executorService = new ThreadPoolExecutor(APP_NUM, APP_NUM, 10,
            TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private static volatile ActiveXComponent[] activeXComponents = new ActiveXComponent[APP_NUM];

    @PostConstruct
    public void init() {
        new Thread(() -> {
            while (true) {
                // 减少10 s
                int current = TIMER.decrementAndGet();

                // 倒计时300s 结束，
                if (current <= 0 && REQUEST_COUNT.get() <= 0) {
                    try {
                        closeApp();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    TIMER.getAndSet(1);
                }

                try {
                    // 等待 10 s
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }).start();
    }

    synchronized Dispatch getDocs() {
        ActiveXComponent activeComponent = activeXComponents[CURRENT_INDEX];

        if (activeComponent == null || activeComponent.m_pDispatch == 0) {
            activeComponent = new ActiveXComponent("Word.Application");
            activeComponent.setProperty("AutomationSecurity", new Variant(3));
            activeComponent.setProperty("Visible", new Variant(false));
            activeXComponents[CURRENT_INDEX] = activeComponent;
        }

        CURRENT_INDEX++;
        if (CURRENT_INDEX >= APP_NUM) {
            CURRENT_INDEX = 0;
        }
        if (log.isDebugEnabled()) {
            log.debug("c----t:{},{}", Thread.currentThread().getId(), activeComponent.m_pDispatch);
        }
        return activeComponent.getProperty("Documents").toDispatch();
    }

    private void closeApp() {
        for (int i = 0; i < activeXComponents.length; i++) {
            ActiveXComponent active = activeXComponents[i];
            if (active != null) {
                try {
                    active.invoke("Quit", new Variant[]{});
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                activeXComponents[i] = null;
            }
        }
    }

    private void incrementAndGet() {
        // 增加调用量
        REQUEST_COUNT.incrementAndGet();
    }

    private void decrementAndGet() {
        // 减少调用量
        REQUEST_COUNT.decrementAndGet();
    }

    void initTime() {
        TIMER.set(time);
    }

    public Integer get() {
        return REQUEST_COUNT.get();
    }

    public void word2pdfPage(String inputFile, String pdfFile) {
        try {
            // 请求数加一
            incrementAndGet();
            Future<Dispatch> future = executorService.submit(new MyThreadGetDoc(this, inputFile, pdfFile));
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        } finally {
            // 请求时减一
            decrementAndGet();
        }
    }
}

@Slf4j
class MyThreadGetDoc implements Callable<Dispatch> {

    private OfficeService officeService;
    private String inputFile;
    private String pdfFile;

    MyThreadGetDoc(OfficeService officeService, String inputFile, String pdfFile) {
        this.officeService = officeService;
        this.inputFile = inputFile;
        this.pdfFile = pdfFile;
    }

    @Override
    public Dispatch call() {
        long start = System.nanoTime();
        Dispatch doc = null;
        try {
            officeService.initTime();
            if (log.isInfoEnabled()) {
                log.info("open doc >>> " + inputFile);
            }
            // Object[]第三个参数是表示“是否只读方式打开”
            // 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
            doc = Dispatch.call(this.officeService.getDocs(), "Open", inputFile, false, true).toDispatch();

            // 调用Document对象的SaveAs方法，将文档保存为pdf格式
            if (log.isInfoEnabled()) {
                log.info("converting: [" + inputFile + "] >>> [" + pdfFile + "]");
            }
            //word保存为pdf格式宏，值为17
            Dispatch.call(doc, "SaveAs", pdfFile, OfficeService.WORD_FORMAT_PDF);

            if (log.isInfoEnabled()) {
                long end = System.nanoTime();
                log.info("cost time：" + (end - start) / 1000000 + "ms.");
            }

        } catch (Exception e) {
            log.error("Error:file convert failed：" + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (doc != null) {
                Dispatch.call(doc, "Close", false);
                if (log.isInfoEnabled()) {
                    log.info("close doc:{}", Thread.currentThread().getId());
                }
            }
        }
        return null;
    }
}

