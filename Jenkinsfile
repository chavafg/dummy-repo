library "pipeline-shared-library@$BRANCH_NAME"

environment {
				// Golang variables:
		                GOPATH="${WORKSPACE}/go"
		                GOROOT="/usr/local/go"
		                PATH="${GOPATH}/bin:/usr/local/go/bin:/usr/sbin:/sbin:${PATH}"

                		// Kata related variables:
		                CI=true
				kata_repo="github.com/kata-containers/tests"
				tests_repo="github.com/kata-containers/tests"
				runtime_repo="github.com/kata-containers/runtime"
				kata_repo_dir="${GOPATH}/src/${kata_repo}"
				tests_repo_dir="${GOPATH}/src/${tests_repo}"

				branch="$CHANGE_TARGET"
				pr_number="$CHANGE_ID"
				pr_branch="PR_${pr_number}"
}

stage("TEsts") {
	parallel ('ubuntu-1604': {
		node ('ubuntu-1604') {
			withEnv(env + [ZUUL=false]) {
				stage('Demo on Ubuntu 16.04') {
					echo 'Hello World'
					sayHello 'Dave'
					sh 'env'
					echo "This is PR $CHANGE_ID"
				}
			}
			stage('Second stage on $NODE_NAME') {
				echo "shalalala"
			}
		}
	}, 'centos-7': {
		node ('centos-7') {
			stage('Demo on Centos 7') {
				echo 'Hello World'
				sayHello 'Dave'
				sh 'env'
				echo "This is PR $CHANGE_ID"
			}
			stage('Second stage on $NODE_NAME') {
				echo "shalalala"
			}
		}
	})
}
