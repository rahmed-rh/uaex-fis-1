openshift.withCluster() {
 openshift.withProject('poc') {
  echo "Hello from project ${openshift.project()} in cluster ${openshift.cluster()}"
  APP_GIT_URL = "https://github.com/skoussou/multi-xpaas-micros-story/fis-rest-1"
  def cm
  node('maven') {


   // Mark the code checkout 'stage'....
   stage('Configure') {

     def cmSelector = openshift.selector("configmap", "app-config")
     def cmExists = cmSelector.exists()


     if (cmExists) {
      cm = cmSelector.object()
     } 
     else {
      def cmappconfig = [
       [
        "kind": "ConfigMap",
        "apiVersion": "v1",
        "metadata": [
         "name": "app-config"
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
    // Mark the code checkout 'stage'....
   stage('Checkout') {

    // Get some code from a GitHub repository
    git branch: "master", url: cm.data['app-git-url']

   }
  }
 }
}