#!/usr/bin/env bash

keytool -genkeypair -keyalg RSA -keysize 1024 -validity 365 \
  -dname "cn=broker, ou=cp530, o=UW, l=Seattle, st=Washington, c=US" \
  -alias brokerPrivKey \
  -storetype PKCS12 -keystore src/main/resources/brokerKey.pkcs12 \
  -storepass brokerStorePass

keytool -export -file src/main/resources/brokerCert.pem -rfc \
  -alias brokerPrivKey -keystore src/main/resources/brokerKey.pkcs12 \
  -storepass brokerStorePass

keytool -genkeypair -keyalg RSA -keysize 1024 -validity 365 \
  -dname "cn=client, ou=cp530, o=UW, l=Seattle, st=Washington, c=US" \
  -alias clientPrivKey \
  -storetype PKCS12 -keystore src/main/resources/clientKey.pkcs12 \
  -storepass clientStorePass

keytool -export -file src/main/resources/clientCert.pem -rfc \
  -alias clientPrivKey -keystore src/main/resources/clientKey.pkcs12 \
  -storepass clientStorePass
