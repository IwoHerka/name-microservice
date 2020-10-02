resource "aws_ecr_repository" "name_microservice" {
    name = "${var.project_name}-name-microservice"
}

output "PLANNING_BOT_REPOSITORY" {
    value = aws_ecr_repository.name_microservice.name
}

output "planning_bot_repository_url" {
    value = aws_ecr_repository.name_microservice.repository_url
}
