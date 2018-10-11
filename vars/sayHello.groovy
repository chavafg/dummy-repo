#!/usr/bin/env groovy

def call(String name = 'human') {
	echo "Hello, ${name}."
	//sh "echo From shell script: ${name}"
}
