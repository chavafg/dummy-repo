pipeline {

	parameters {
		string(name: 'Label', defaultValue: 'centos-7', description: 'Node Label')
	}

	agent { label "${params.Label}" }

	environment {
		// Golang variables:
		GOPATH="${WORKSPACE}/go"
		GOROOT="/usr/local/go"
		PATH="${GOPATH}/bin:/usr/local/go/bin:/usr/sbin:/sbin:${PATH}"

		// Kata related variables:
		CI="true"
		kata_repo="github.com/kata-containers/tests"
		tests_repo="github.com/kata-containers/tests"
		runtime_repo="github.com/kata-containers/runtime"
		kata_repo_dir="${GOPATH}/src/${kata_repo}"
		tests_repo_dir="${GOPATH}/src/${tests_repo}"

		branch="$CHANGE_TARGET"
		pr_number="$CHANGE_ID"
//		pr_branch="PR_${pr_number}"
		pr_branch="master"
	}

	stages {
		stage('Setup repo environment') {
			steps {
				sh '''
				mkdir -p "${GOPATH}"
				mkdir -p $(dirname "${tests_repo_dir}")
				mkdir -p $(dirname "${kata_repo_dir}")
				[ -d "${tests_repo_dir}" ] || git clone "https://${tests_repo}.git" "${tests_repo_dir}"
				[ -d "${kata_repo_dir}" ] || git clone "https://${kata_repo}.git" "${kata_repo_dir}"
				'''
       //                       cd "${kata_repo_dir}"
                       //       fetch origin "pull/${pr_number}/head:${pr_branch}"
                       //       git checkout "${pr_branch}"
                       //       git rebase "origin/${branch}"
                        }
                }

		stage('Install Golang') {
			steps {
				sh "echo Parameter passed: ${params.Label}"
				sh '''
				"${GOPATH}/src/${tests_repo}/.ci/install_go.sh" -p -f
				go version
				'''
			}
		}

		stage('Install Kata dependencies') {
			steps {
				sh '${GOPATH}/src/${tests_repo}/.ci/resolve-kata-dependencies.sh'
			}
		}

		stage('Setup distro environment') {
			steps {
				sh '''
				source /etc/os-release
				cd "${GOPATH}/src/${tests_repo}"
				".ci/setup_env_${ID}.sh"
				'''
			}
		}

		stage('Install Docker') {
			steps {
				sh '''
				source /etc/os-release
				source "${tests_repo_dir}/.ci/lib.sh"
				if ! command -v docker >/dev/null; then
					"${tests_repo_dir}/cmd/container-manager/manage_ctr_mgr.sh" docker install
				fi
				# If on CI, check that docker version is the one defined
				# in versions.yaml. If there is a different version installed,
				# install the correct version..
				docker_version=$(get_version "externals.docker.version")
				docker_version=${docker_version/v/}
				docker_version=${docker_version/-*/}
				if ! sudo docker version | grep -q "$docker_version" && [ "$CI" == true ]; then
					"${tests_repo_dir}/cmd/container-manager/manage_ctr_mgr.sh" docker install -f
				fi
				'''
			}
		}

		stage('Enable nested virtualization') {
			steps {
				sh'''
				if [ "$CI" == true ] && grep -q "N" /sys/module/kvm_intel/parameters/nested 2>/dev/null; then
					echo "enable Nested Virtualization"
					sudo modprobe -r kvm_intel
					sudo modprobe kvm_intel nested=1
					if grep -q "N" /sys/module/kvm_intel/parameters/nested 2>/dev/null; then
						die "Failed to find or enable Nested virtualization"
					fi
				fi
				'''
			}
		}

		stage('Install Kata Containers components') {
			steps {
				sh'''
				bash -f ${tests_repo_dir}/.ci/install_kata.sh
				'''
			}
		}

		stage('Install testing dependencies') {
			steps {
				sh'''
				bash -f "${tests_repo_dir}/.ci/install_cni_plugins.sh"
				bash -f "${tests_repo_dir}/.ci/install_crio.sh"
				bash -f "${tests_repo_dir}/.ci/install_cri_containerd.sh"
				bash -f "${tests_repo_dir}/.ci/install_kubernetes.sh"
				bash -f "${tests_repo_dir}/.ci/install_openshift.sh"
				sudo crudini --set /etc/systemd/journald.conf Journal RateLimitInterval 0s
				sudo crudini --set /etc/systemd/journald.conf Journal RateLimitBurst 0
				sudo systemctl restart systemd-journald
				sync
				sudo -E PATH=$PATH bash -c "echo 3 > /proc/sys/vm/drop_caches"
				'''
			}
		}

	}
}
