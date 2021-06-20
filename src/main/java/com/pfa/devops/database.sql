CREATE TABLE `user` (
                            `user_id` int(11) NOT NULL AUTO_INCREMENT,
                            `user_name` varchar(50) DEFAULT NULL,
                            `user_password` varchar(50) DEFAULT NULL,
                            `user_email` varchar(50) DEFAULT NULL,
                            `user_slack_id` varchar(50) DEFAULT NULL,
                            `user_github_id` varchar(50) DEFAULT NULL,
                            PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

CREATE TABLE `project` (
                           `project_id` int(11) NOT NULL AUTO_INCREMENT,
                           `project_title` varchar(100) DEFAULT NULL,
                           `project_github_repo` varchar(100) DEFAULT NULL,
                           `project_docker_repo` varchar(100) DEFAULT NULL,
                           `project_language` varchar(100) DEFAULT NULL,
                           `project_jenkins_uri` varchar(100) DEFAULT NULL,
                           `project_email` varchar(100) DEFAULT NULL,
                           `project_description` varchar(200) DEFAULT NULL,
                           `project_statue` boolean DEFAULT false,
                           `project_type` int(11) DEFAULT NULL,
                           PRIMARY KEY (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

CREATE TABLE `user_project` (
                                    `user_id` int(11) NOT NULL,
                                    `project_id` int(11) NOT NULL,
                                    PRIMARY KEY (`user_id`,`project_id`),
                                    KEY `project_id` (`project_id`),
                                    CONSTRAINT `employee_project_ibfk_1`
                                        FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
                                    CONSTRAINT `employee_project_ibfk_2`
                                        FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;