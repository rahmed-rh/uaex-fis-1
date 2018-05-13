openshift.withCluster() {
    openshift.withCredentials('cluster-admin-credential-id') {
        def PROJECT_NAME = "poc"
        def amqTemplate
        def amqModels
        def cm


        openshift.withProject(PROJECT_NAME) {
        	def APP_GIT_URL = "https://github.com/rahmed-rh/uaex-fis-1"
            def FIS_IMAGE_STREAM = "https://raw.githubusercontent.com/jboss-fuse/application-templates/GA/fis-image-streams.json"
            def AMQ_IMAGE_STREAM = "https://raw.githubusercontent.com/jboss-openshift/application-templates/master/amq/amq63-image-stream.json"
            def AMQ_TEMPLATE = "https://raw.githubusercontent.com/jboss-openshift/application-templates/master/amq/amq63-persistent.json"
            def AMQ_APP_NAME = "broker-uaex"
        
            echo "Hello from project ${openshift.project()} in cluster ${openshift.cluster()}"

            // Mark the code checkout 'stage'....
            stage('Read CM') {

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
                                "amq-template": "${AMQ_TEMPLATE}",
                                "amq-app-name": "${AMQ_APP_NAME}"
                            ]
                        ]
                    ]
                    cm = openshift.create(cmappconfig).object()

                }
                echo "The CM is ${cm}"
                echo "The app-git-url is ${cm.data['app-git-url']}"

            }
            stage('Create Service Account for AMQ') {
                //create amq servie account
                def amqSASelector = openshift.selector("serviceaccount", "amq-service-account")
                def amqSAExists = amqSASelector.exists()
                if (!amqSAExists) {
                    openshift.create('serviceaccount', 'amq-service-account')
                }

                openshift.policy("add-role-to-user", "view", "system:serviceaccount:$PROJECT_NAME:amq-service-account", "-n", PROJECT_NAME)
            }

        }

        openshift.withProject("openshift") {
            
            // Should i really check if template or is exists or no, or just go with the latest as the is maybe update !!!
            //create FIS builder imagestream
            //def fisISSelector = openshift.selector("imagestream", "fis-java-openshift")
            //def fisISExists = cmSelector.exists()
            //if (!fisISExists) {
            //openshift.replace("--force", "-f ", cm.data['fis-image-stream'])
            //}

            //create AMQ builder imagestream & template
            //def amqTemplateSelector = openshift.selector("template", "amq63-persistent")
            //def amqTemplateExists = amqTemplateSelector.exists()
            //if (!amqTemplateExists) {
            //amqTemplate = openshift.replace("--force", "-f ", cm.data['amq-template'])
            //}

            //def amqISSelector = openshift.selector("imagestream", "amq63-image-stream")
            //def amqISExists = amqISSelector.exists()
            //if (!amqISExists) {
            //openshift.replace("--force", "-f ", cm.data['amq-image-stream'])
            //}

            //amqModels = openshift.process("amq63-persistent", "-p APPLICATION_NAME=${cm.data['amq-app-name']} -p AMQ_STORAGE_USAGE_LIMIT=5gb", "-p MQ_USERNAME=admin", "-p MQ_PASSWORD=passw0rd", "-p MQ_QUEUES=TESTQUEUE")

        }

        openshift.withProject(PROJECT_NAME) {
            // Create the AMQ....
            stage('Create the AMQ') {

				//objects = openshift.create(amqModels)
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
                    sh "mvn clean compile -s settings.xml"
                }
                stage('Deploy to DEV') {
                    // Run the fabric8
                    sh "mvn fabric8:deploy -s settings.xml"
                }
                
            }
        }

    }
}