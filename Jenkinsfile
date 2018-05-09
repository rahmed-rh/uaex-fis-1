openshift.withCluster() {
 def PROJECT_NAME = "poc"
 openshift.withProject(PROJECT_NAME) {
  echo "Hello from project ${openshift.project()} in cluster ${openshift.cluster()}"
  def APP_GIT_URL = "https://github.com/rahmed-rh/uaex-fis-1"
  def FIS_IMAGE_STREAM = "https://raw.githubusercontent.com/jboss-fuse/application-templates/GA/fis-image-streams.json"
  def AMQ_IMAGE_STREAM = "https://raw.githubusercontent.com/jboss-openshift/application-templates/master/amq/amq63-image-stream.json"
  def AMQ_TEMPLATE = "https://raw.githubusercontent.com/jboss-openshift/application-templates/master/amq/amq63-persistent.json"
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
       "app-git-url": "${APP_GIT_URL}",
       "fis-image-stream": "${FIS_IMAGE_STREAM}",
       "amq-image-stream": "${AMQ_IMAGE_STREAM}",
       "amq-template": "${AMQ_TEMPLATE}"
      ]
     ]
    ]
    cm = openshift.create(cmappconfig).object()

   }
   echo "The CM is ${cm}"
   echo "The app-git-url is ${cm.data['app-git-url']}"


   // Should i really check if template or IS exists or not or just go with the latest as the IS maybe update !!!
   //create FIS builder imagestream
   //def fisISSelector = openshift.selector("imagestream", "fis-java-openshift")
   //def fisISExists = cmSelector.exists()
   //if (!fisISExists) {
   openshift.replace(cm.data['fis-image-stream'], "-f")
    //}

   //create AMQ builder imagestream & template
   //def amqTemplateSelector = openshift.selector("template", "amq63-persistent")
   //def amqTemplateExists = amqTemplateSelector.exists()
   //if (!amqTemplateExists) {
   openshift.replace(cm.data['amq-template'], "-f")
    //}

   //def amqISSelector = openshift.selector("imagestream", "amq63-image-stream")
   //def amqISExists = amqISSelector.exists()
   //if (!amqISExists) {
   openshift.replace(cm.data['amq-image-stream'], "-f")
    //}

   //create amq servie account
   def amqSASelector = openshift.selector("serviceaccount", "amq-service-account")
   def amqSAExists = amqSASelector.exists()
   if (!amqISExists) {
    openshift.create('serviceaccount', 'amq-service-account')
   }
   
   sh "oc policy add-role-to-user view system:serviceaccount:$PROJECT_NAME:amq-service-account"
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
   }
   stage('Deploy to DEV') {
    // Run the fabric8
    sh "mvn fabric8:deploy"
   }
  }
 }
}