#! /bin/sh

missing_env_var_secret=false

CONTEXT_FILE_PATH=/usr/local/tomcat/conf/context.xml

verify_and_write_secret() {
  filename=$(basename "$1")

  if [ "$filename" = "kubernetes.io" ]; then
    continue
  fi

  word_count=$(wc -w $1 | cut -f1 -d" ")
  line_count=$(wc -l $1 | cut -f1 -d" ")

  if [ ${word_count} != 1 ] || [ ${line_count} != 1 ]; then
    echo Error with secret $1. He contains $word_count word and $line_count line
    exit 1
  fi

  value=$(cat ${1})
  sed -i "s|\${$2}|$value|" ${CONTEXT_FILE_PATH}
}

#Verify secrets
if ! [ -f ${KHEOPS_AUTHDB_PASS_FILE} ]; then
    echo "Missing kheops_authdb_pass secret"
    missing_env_var_secret=true
fi
if ! [ -f ${KHEOPS_AUTH_HMASECRET_FILE} ]; then
    echo "Missing kheops kheops_auth_hmasecret secret"
    missing_env_var_secret=true
fi
if ! [ -f ${KHEOPS_CLIENT_DICOMWEBPROXY_SECRET_FILE} ]; then
    echo "Missing kheops_client_dicomwebproxysecret secret"
    missing_env_var_secret=true
fi
if ! [ -f ${KHEOPS_CLIENT_ZIPPER_SECRET_FILE} ]; then
    echo "Missing kheops_client_zippersecret secret"
    missing_env_var_secret=true
fi

#Verify environment variables
if [ -z "$KHEOPS_AUTHDB_USER" ]; then
    echo "Missing KHEOPS_AUTHDB_USER environment variable"
    missing_env_var_secret=true
fi
if [ -z "$KHEOPS_AUTHDB_URL" ]; then
    echo "Missing KHEOPS_AUTHDB_URL environment variable"
    missing_env_var_secret=true
fi
if [ -z "$KHEOPS_AUTHDB_NAME" ]; then
    echo "Missing KHEOPS_AUTHDB_NAME environment variable"
    missing_env_var_secret=true
fi
if [ -z "$KHEOPS_ROOT_URL" ]; then
    echo "Missing KHEOPS_ROOT_URL environment variable"
    missing_env_var_secret=true
fi
if [ -z "$KHEOPS_PACS_PEP_URL" ]; then
    echo "Missing KHEOPS_PACS_PEP_URL environment variable"
    missing_env_var_secret=true
fi
if [ -z "$KHEOPS_OIDC_PROVIDER" ]; then
    echo "Missing KHEOPS_OIDC_PROVIDER environment variable"
    missing_env_var_secret=true
fi
if [ -z "$KHEOPS_CLIENT_DICOMWEBPROXYCLIENTID" ]; then
    echo "Missing KHEOPS_CLIENT_DICOMWEBPROXYCLIENTID environment variable"
    missing_env_var_secret=true
fi
if [ -z "$KHEOPS_CLIENT_ZIPPERCLIENTID" ]; then
    echo "Missing KHEOPS_CLIENT_ZIPPERCLIENTID environment variable"
    missing_env_var=true
fi

kheops_welcomebot_webhook=""
if [ -z "$KHEOPS_WELCOMEBOT_WEBHOOK" ]; then
    echo "No KHEOPS_WELCOMEBOT_WEBHOOK environment variable, welcomebot is disable"
else
    kheops_welcomebot_webhook=$KHEOPS_WELCOMEBOT_WEBHOOK
fi

use_scope=true
if [ -z "$KHEOPS_USE_KHEOPS_SCOPE" ]; then
    echo "KHEOPS_USE_KHEOPS_SCOPE not set default value is 'true'"

else
    use_scope=false
fi

#if missing env var or secret => exit
if [ "$missing_env_var_secret" = true ]; then
    exit 1
fi

#get secrets and verify content
verify_and_write_secret $KHEOPS_AUTHDB_PASS_FILE kheops_authdb_pass
verify_and_write_secret $KHEOPS_AUTH_HMASECRET_FILE kheops_auth_hmasecret
verify_and_write_secret $KHEOPS_CLIENT_DICOMWEBPROXY_SECRET_FILE kheops_client_dicomwebproxysecret
verify_and_write_secret $KHEOPS_CLIENT_ZIPPER_SECRET_FILE kheops_client_zippersecret

#get env var

sed -i "s|\${kheops_root_url}|$KHEOPS_ROOT_URL|" ${CONTEXT_FILE_PATH}
sed -i "s|\${kheops_postgresql_user}|$KHEOPS_AUTHDB_USER|" ${CONTEXT_FILE_PATH}
sed -i "s|\${kheops_postgresql_url}|$KHEOPS_AUTHDB_URL/$KHEOPS_AUTHDB_NAME|" ${CONTEXT_FILE_PATH}
sed -i "s|\${kheops_pacs_url}|$KHEOPS_PACS_PEP_URL|" ${CONTEXT_FILE_PATH}

sed -i "s|\${kheops_client_dicomwebproxyclientid}|$KHEOPS_CLIENT_DICOMWEBPROXYCLIENTID|" ${CONTEXT_FILE_PATH}
sed -i "s|\${kheops_client_zipperclientid}|$KHEOPS_CLIENT_ZIPPERCLIENTID|" ${CONTEXT_FILE_PATH}
sed -i "s|\${kheops_oidc_provider}|$KHEOPS_OIDC_PROVIDER|" ${CONTEXT_FILE_PATH}
sed -i "s|\${kheops_use_kheops_scope}|$use_scope|" ${CONTEXT_FILE_PATH}
sed -i "s|\${kheops_welcomebot_webhook}|$KHEOPS_WELCOMEBOT_WEBHOOK|" ${CONTEXT_FILE_PATH}



echo "Ending setup secrets and env var"

#######################################################################################
#ELASTIC SEARCH

if ! [ -z "$KHEOPS_AUTHORIZATION_ENABLE_ELASTIC" ]; then
    if [ "$KHEOPS_AUTHORIZATION_ENABLE_ELASTIC" = true ]; then

        echo "Start init filebeat and metricbeat"
        missing_env_var_secret=false

        if [ -z "$KHEOPS_AUTHORIZATION_ELASTIC_INSTANCE" ]; then
          echo "Missing KHEOPS_AUTHORIZATION_ELASTIC_INSTANCE environment variable"
          missing_env_var_secret=true
        else
           echo "environment variable KHEOPS_AUTHORIZATION_ELASTIC_INSTANCE \e[92mOK\e[0m"
           sed -i "s|\${instance}|$KHEOPS_AUTHORIZATION_ELASTIC_INSTANCE|" /etc/filebeat/filebeat.yml
           sed -i "s|\${instance}|$KHEOPS_AUTHORIZATION_ELASTIC_INSTANCE|" /etc/metricbeat/metricbeat.yml
        fi

        if [ -z "$KHEOPS_AUTHORIZATION_LOGSTASH_URL" ]; then
          echo "Missing KHEOPS_AUTHORIZATION_LOGSTASH_URL environment variable"
          missing_env_var_secret=true
        else
           echo "environment variable KHEOPS_AUTHORIZATION_LOGSTASH_URL \e[92mOK\e[0m"
           sed -i "s|\${logstash_url}|$KHEOPS_AUTHORIZATION_LOGSTASH_URL|" /etc/filebeat/filebeat.yml
           sed -i "s|\${logstash_url}|$KHEOPS_AUTHORIZATION_LOGSTASH_URL|" /etc/metricbeat/metricbeat.yml
        fi

        #if missing env var or secret => exit
        if [ $missing_env_var_secret = true ]; then
          exit 1
        else
           echo "all elastic secrets and all env var \e[92mOK\e[0m"
        fi

        filebeat modules disable system
        service filebeat restart
        metricbeat modules disable system
        service metricbeat restart

        echo "Ending setup FILEBEAT and METRICBEAT"
    fi
else
    echo "[INFO] : Missing KHEOPS_AUTHORIZATION_ENABLE_ELASTIC environment variable. Elastic is not enable."
fi

#######################################################################################


#run tomcat
catalina.sh run;
