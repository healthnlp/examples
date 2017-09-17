#!/bin/bash

cTAKES_DICTIONARY_HOME=target/ctakes-web-client/WEB-INF/classes/org/apache/ctakes/dictionary
DICTIONARY_FAST_HOME=${cTAKES_DICTIONARY_HOME}/lookup/fast

cTAKESHSQL_CONF=${DICTIONARY_FAST_HOME}/cTakesHsql.xml
SNO_RX_16AB_CONF=${DICTIONARY_FAST_HOME}/sno_rx_16ab.xml

CHANGEME_FILES=( "${cTAKESHSQL_CONF}" "${SNO_RX_16AB_CONF}" )

for changeme_file in "${CHANGEME_FILES[@]}"; do
  if [ ! -f "${changeme_file}" ]; then
    echo "dictionary ${changeme_file} could not be found"
    continue
  fi

  echo "updating credentials in dictionary configuration ${changeme_file}"

  # updating umlsUser
  sed -i "s/\(<property key=\"umlsUser\" value=\"\)CHANGEME/\1${ctakes_umlsuser}/g" "${changeme_file}"

  # updating umlsPass
  sed -i "s/\(<property key=\"umlsPass\" value=\"\)CHANGEME/\1${ctakes_umlspassword}/g" "${changeme_file}"
done
