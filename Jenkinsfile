pipeline {
	agent any
	stages {
		stage('Clone') {
			steps {
				checkout scm
			}
		}
		stage('Build') {
			steps {
				sh './gradlew setupCiWorkspace clean build'
				archive 'build/libs/*jar'
			}
		}
	}
}
