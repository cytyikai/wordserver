#
# Copyright (C) 2018 Xuetong Tech., Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

FROM java:8
MAINTAINER root

ARG JAR_FILE

COPY ${JAR_FILE} /usr/local/app.jar

WORKDIR /var/app

EXPOSE 8015

ENV JAVA_OPTS="-Xms512m -Xmx1g -XX:MaxDirectMemorySize=1g -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF8 -Duser.timezone=GMT+08"

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /usr/local/app.jar" ]
