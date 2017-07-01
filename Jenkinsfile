pipeline {
	agent any
	stages {
		stage('Build') {
			steps {
				sh './gradlew setupCiWorkspace clean build'
				archive 'build/libs/*jar'
			}
		}
		stage('Deploy') {
			steps {
				withCredentials([string(credentialsId: 'privateGradle', variable: 'PRIVATEGRADLE')]) {
					sh '''
						echo -n "$PRIVATEGRADLE" > private.gradle
						./gradlew upload
					'''
				}
			}
		}
	}
}
