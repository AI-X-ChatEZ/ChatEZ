REPOSITORY=/home/ubuntu/app
cd $REPOSITORY

APP_NAME=demo
WAR_NAME=$(ls $REPOSITORY/build/libs/ | grep '.war' | tail -n 1)
WAR_PATH=$REPOSITORY/build/libs/$WAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  sudo kill -15 $CURRENT_PID
  sleep 5
fi

echo "> $WAR_PATH 배포"
nohup java -jar \
        -Dspring.profiles.active=dev \
        build/libs/$WAR_NAME > $REPOSITORY/nohup.out 2>&1 &
