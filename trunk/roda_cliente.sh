#!/bin/bash


ERR_JDK=1
ERR_JAVA=2
ERR_CLASSPATH=3
ERR_CODEBASE=4
ERR_JPOLICY=5
ERR_IMAGEM=6

LOCAL=`pwd`
JDK="/opt/jdk1.6.0_10"
JAVA="$JDK/bin/java"
CLASSPATH="$LOCAL/bin"
CODEBASE="http://www2.dc.uel.br/~rpherrera/trabalhos_so/"
JPOLICY="$LOCAL/java.policy"
IMAGEM="$LOCAL/imagem.jpg"


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


checa_existencia $JDK       $ERR_JDK
checa_existencia $JAVA      $ERR
checa_existencia $CLASSPATH $ERR
checa_existencia $JPOLICY   $ERR
checa_existencia $IMAGEM    $ERR


pushd $CLASSPATH


echo "Iniciando a comunicacao com o 'Servidor'"
java -Djava.rmi.server.codebase=$CODEBASE  \
     -Djava.security.policy=$JPOLICY       \
     rmi.CircCliente $HOST1 $HOST2 $IMAGEM

popd

