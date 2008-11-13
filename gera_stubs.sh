#!/bin/bash


ERR_JDK=1
ERR_RMIC=2
ERR_CLASSPATH=3
ERR_GERACAO=4

JDK="/opt/jdk1.6.0_10"
RMIC="$JDK/bin/rmic"
CLASSPATH="`pwd`/bin"


function checa_existencia {
    echo -n "Checando a existencia de '$1'..."
    if [ ! -e $1 ];
    then
        echo -e "\nErro: '$1' nao existe. Abortando a execucao."
        exit $2
    else
        echo " [OK]"
    fi
}


checa_existencia $JDK	    $ERR_JDK
checa_existencia $RMIC      $ERR_RMIC
checa_existencia $CLASSPATH $ERR_CLASSPATH


pushd $CLASSPATH >/dev/null 2>&1


echo "Iniciando a geracao de um 'stub' para 'CircServidor'..."
$RMIC rmi.CircServidor || exit $ERR_GERACAO
echo "Stub gerado com sucesso!"


popd >/dev/null 2>&1

