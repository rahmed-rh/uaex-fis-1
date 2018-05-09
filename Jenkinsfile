openshift.withCluster() {
 openshift.withProject('poc') {
  echo "Hello from project ${openshift.project()} in cluster ${openshift.cluster()}"
  APP_GIT_URL = "https://github.com/rahmed-rh/uaex-fis-1"
  def cm

  // Mark the code checkout 'stage'....
  stage('Configure') {

   def cmSelector = openshift.selector("configmap", "pipeline-app-config")
   def cmExists = cmSelector.exists()


   if (cmExists) {
    cm = cmSelector.object()
   } else {
    def cmappconfig = [
     [
      "kind": "ConfigMap",
      "apiVersion": "v1",
      "metadata": [
       "name": "pipeline-app-config"
      ],
      "data": [
       "app-git-url": "${APP_GIT_URL}"
      ]
     ]
    ]
    cm = openshift.create(cmappconfig).object()

   }
   echo "The CM is ${cm}"
   echo "The CM is ${cm.data['app-git-url']}"

  }
  node('maven') {
   // Mark the code checkout 'stage'....
   stage('Checkout') {

     // Get some code from a GitHub repository
     git branch: "master", url: cm.data['app-git-url']
    }
    // Mark the code build 'stage'....
   stage('Maven Build') {
    // Run the maven build
    sh "mvn clean compile"
    sh "mvn fabric8:deploy"
   }
  }
 }
}