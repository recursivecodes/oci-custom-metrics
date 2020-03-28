# dbaas-metrics

This is an example of using the OCI Java SDK to publish custom metrics in the Oracle Cloud. This example runs a job every 60 seconds to publish DB load and storage numbers.

## ENV vars

Some vars necessary for running locally and in the Oracle Cloud

```bash
export DATASOURCES_DEFAULT_PASSWORD=[DBaaS password]
export DATASOURCES_DEFAULT_URL=[JDBC connect string]
export CODES_RECURSIVE_OCI_PROFILE=[For use when running locally - Default=DEFAULT]
export CODES_RECURSIVE_METRICS_NAMESPACE=[The namespace used to publish your custom metrics - whatever you want to use]
export CODES_RECURSIVE_USE_INSTANCE_PRINCIPAL=[Set to true when deployed on Oracle Cloud]
export CODES_RECURSIVE_METRICS_COMPARTMENT_OCID=[The compartment you want to publish your metrics in]
export CODES_RECURSIVE_DB_OCID=[Your DB OCID]
export DATASOURCES_DEFAULT_USERNAME=[DBaaS username]
export CODES_RECURSIVE_OCI_CONFIG_PATH=[The path to your local OCI config - Default=~/.oci/config]
```
