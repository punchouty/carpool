rm -rf /var/racloop/es/data
rm -rf /var/racloop/es/work
rm -rf /var/racloop/es/logs

export GRAILS_HOME=/Users/rpunch/Documents/ggts-bundle/grails-2.3.5
cd /Users/rpunch/Documents/workspace-ggts/carpool
$GRAILS_HOME/bin/grails run-app