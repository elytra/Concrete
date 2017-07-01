pipeline {
	agent any
	stages {
		stage('Clone') {
			checkout scm
		}
		stage('Build') {
			sh './gradlew setupCiWorkspace clean build'
			archive 'build/libs/*jar'
		}
	}
}
