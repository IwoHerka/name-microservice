provider "aws" {
  region = var.aws_region
}

provider "random" {}

terraform {
  backend "s3" {
    bucket = "makimo-deployments"
    key    = "name-microservice/staging"
    region = "eu-west-1"
  }
}
