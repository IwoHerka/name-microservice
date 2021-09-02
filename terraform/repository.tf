resource "aws_ecr_repository" "name_microservice" {
  name = "${var.project_name}-name-microservice"
}
