variable "project_name" {
  default = "planning-bot"
}

variable "aws_region" {
  default = "eu-west-1"
}

variable "common_tags" {
  default = {
    Project : "SARR",
    Repository : "planning-slack-bot",
  }
}
