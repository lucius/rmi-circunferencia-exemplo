#!/bin/bash


ERR_JDK=1
ERR_RMI_REG=2
ERR_JAVA=3
ERR_CLASSPATH=4
ERR_IMAGEM=5


JDK="/opt/jdk1.6.0_10"
RMI_REG="$JDK/bin/rmiregistry"
JAVA="$JDK/bin/java"
CLASSPATH="`pwd`/bin"
IMAGEM="`pwd`/imagem.jpg"


HOST1="servidor1"
HOST2="servidor2"
PORTA="9999"


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
    ps -p $RMI_REG_PID >/dev/null 2>&1
    if [ $? ]
    then
        echo "Matando o processo 'rmiregistry' numero $RMI_REG_PID..."
	kill -s SIGKILL $RMI_REG_PID >/dev/null 2>&1
    fi
}

checa_existencia $JDK	    $ERR_JDK
checa_existencia $RMI_REG   $ERR_RMI_REG
checa_existencia $JAVA      $ERR_JAVA
checa_existencia $CLASSPATH $ERR_CLASSPATH


echo "Inicializando, em background, o 'rmiregistry'"
pushd $CLASSPATH >/dev/null 2>&1
$RMI_REG
$RMI_REG_PID=$!
trap limpa_rmi SIGINT


sleep 1


echo "Inicializando 2 'Servidores' como '$HOST1' e '$HOST2'"
java \
-Djava.rmi.server.codebase=http://www2.dc.uel.br/~rpherrera/trabalhos_so/ \
-Djava.security.policy=../java.policy rmi.CircServidor $HOST1 &
sleep 1
java \
-Djava.rmi.server.codebase=http://www2.dc.uel.br/~rpherrera/trabalhos_so/ \
-Djava.security.policy=../java.policy rmi.CircServidor $HOST2 &
sleep 1
echo ""


# TODO Separar em um script cliente
echo "Iniciando a comunicacao com o 'Servidor'"
java \
-Djava.rmi.server.codebase=http://www2.dc.uel.br/~rpherrera/trabalhos_so/  \
-Djava.security.policy=../java.policy \
rmi.CircCliente $HOST1 $HOST2 $IMAGEM


if [ ! $? ]
then
    limpa_rmi
fi


popd >/dev/null 2>&1


