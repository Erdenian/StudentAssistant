pipeline {
  agent {
    node {
      label 'android'
    }
  }

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
        junit '*/build/test-results/testDebugUnitTest/*.xml'
      }
    }
    stage('Assemble') {
      steps {
        sh './gradlew assembleDebug'
        archiveArtifacts '*/build/outputs/apk/debug/*.apk'
      }
    }
  }
}