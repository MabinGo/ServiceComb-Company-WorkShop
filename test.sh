#!/bin/bash

curl -D /tmp/headers.txt -s -o /dev/null  -w "%{http_code}" -d "username=jordan&password=password" -H "Content-Type: application/x-www-form-urlencoded" -XPOST "http://$HOST/doorman/rest/login"
Authorization=`cat /tmp/headers.txt | grep "Authorization" | awk '{print $2,$3}'`
echo "$http_code"
if [ -z "$Authorization" ];then
  echo " get token failed"
  exit 0
fi

while true
do
#  echo "get fibonacci number"
  sleep 1
  FIBONA_NUM1=`curl -s -H "Authorization: $Authorization" -XGET "http://$HOST/worker/fibonacci/term?n=6"`
  if [ -z "$FIBONA_NUM1" ];then
    echo "Get fibonacci number failed"
    exit 0
  fi

  FIBONA_NUM2=`curl -s -H "Authorization: $Authorization" -XGET "http://$HOST/beekeeper/rest/drone/ancestors/30"`
  if [ -z "$FIBONA_NUM2" ];then
    echo "Get drone failed"
    exit 0
  fi

  FIBONA_NUM3=`curl -s -H "Authorization: $Authorization" -XGET "http://$HOST/beekeeper/rest/queen/ancestors/30"`
  if [ -z "$FIBONA_NUM3" ];then
    echo "Get queen number failed"
    exit 0
  fi
  echo $FIBONA_NUM1
  echo $FIBONA_NUM2
  echo $FIBONA_NUM3
done
