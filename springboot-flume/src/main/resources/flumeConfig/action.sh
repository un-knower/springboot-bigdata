#!/usr/bin/env bash

#参数赋值
v_conf_path=$FLUME_HOME/conf
v_conf_name=tomcat.conf
v_agent_name=a1

flume-ng agent -c conf/ -f $v_conf_path/$v_conf_name  -n $v_agent_name -Dflume.root.logger=ERROR &
