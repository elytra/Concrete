pipeline {
	agent any
	stages {
		stage('Build') {
			steps {
				sh './gradlew setupCiWorkspace clean build'
				archive 'build/libs/*jar'
			}
		}
	}
}
