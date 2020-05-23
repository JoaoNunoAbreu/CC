#!/bin/bash
printf "\033c"
rm -rf log_atena
java AnonGWLauncher 10.3.3.1 80 10.1.1.3 10.3.3.3 10.4.4.2
