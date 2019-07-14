pipeline {
  agent any

  stages {
    stage('Info') {
      steps {
        sh './gradlew printVersion'
      }
    }

    stage('Analysis') {
      parallel {
        stage('Detekt') {
          steps {
            sh './gradlew detekt'
            archiveArtifacts 'build/reports/detekt/detekt.html'
          }
        }

        stage('Lint') {
          steps {
            sh './gradlew lintDebug'
            archiveArtifacts '*/build/reports/lint-results*.html'
          }
        }
      }
    }

    stage('UnitTest') {
      steps {
        sh './gradlew testDebugUnitTest'
        archiveArtifacts '*/build/reports/tests/testDebugUnitTest/'
      }
    }

    stage('Assemble') {
      steps {
        sh './gradlew assembleDebug'
        archiveArtifacts 'app/build/outputs/apk/debug/*.apk'
      }
    }
  }
}