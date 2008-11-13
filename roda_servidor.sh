#!/bin/bash


ERR_JDK=1
ERR_RMI_REG=2
ERR_JAVA=3
ERR_CLASSPATH=4


JDK="/opt/jdk1.6.0_10"
RMI_REG="$JDK/bin/rmiregistry"
JAVA="$JDK/bin/java"
CLASSPATH="`pwd`/bin"


HOST1="servidor1"
HOST2="servidor2"


function checa_existencia {
    echo -n "Checando a existencia de '$1'..."
    if [ ! -e $1 ]
    then
        echo -e "\nErro: '$1' nao existe. Abortando a execucao."
        exit $2
    else
        echo -e " [OK]"
    fi
}


function limpa_rmi {
	echo "Matando os processos 'rmiregistry', '$HOST1' e '$HOST2'"
	kill -s SIGKILL $RMI_REG_PID $SERV1_PID $SERV2_PID >/dev/null 2>&1 &
}


checa_existencia $JDK	    $ERR_JDK
checa_existencia $RMI_REG   $ERR_RMI_REG
checa_existencia $JAVA      $ERR_JAVA
checa_existencia $CLASSPATH $ERR_CLASSPATH


echo "Inicializando em background o 'rmiregistry'"
pushd $CLASSPATH >/dev/null 2>&1
$RMI_REG >/dev/null 2>&1 &
RMI_REG_PID=$!
trap limpa_rmi SIGINT
sleep 1


echo "Registrando 2 servidores como '$HOST1' e '$HOST2'"
$JAVA \
-Djava.rmi.server.codebase=http://www2.dc.uel.br/~rpherrera/trabalhos_so/ \
-Djava.security.policy=../java.policy rmi.CircServidor $HOST1 &
SERV1_PID=$!


$JAVA \
-Djava.rmi.server.codebase=http://www2.dc.uel.br/~rpherrera/trabalhos_so/ \
-Djava.security.policy=../java.policy rmi.CircServidor $HOST2 &
SERV2_PID=$!


wait


popd >/dev/null 2>&1

