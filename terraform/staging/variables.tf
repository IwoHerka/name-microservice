variable "project_name" {
  default = "planning-bot"
}

variable "aws_region" {
  default = "eu-west-1"
}

variable "ecs_cluster" {
  default = "kopytko-cluster"
}

variable "vpc_id" {
  default = "vpc-099dfeb9a2cad8e75"
}

variable "subnet_ids" {
  default = [
    "subnet-0108d294f666a5c26",
    "subnet-0e8eafc25f6dce440",
  ]
}

variable "security_group_ids" {
  default = ["sg-04614de3361c32c8f"]
}

variable "db_host" {
  default = "ec2-54-194-0-58.eu-west-1.compute.amazonaws.com"
}

variable "db_port" {
  default = 27017
}

variable "task_execution_role_name" {
  default = "kopytko-ecs-task-execution"
}

variable "common_tags" {
  default = {
    Environment : "Staging",
    Project : "PlanningBot",
  }
}
