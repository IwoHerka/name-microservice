provider "aws" {
  region = var.aws_region

  default_tags {
    tags = var.common_tags
  }
}

provider "random" {}

terraform {
  backend "s3" {
    bucket = "makimo-deployments"
    key    = "name-microservice/staging"
    region = "eu-west-1"
  }
}
