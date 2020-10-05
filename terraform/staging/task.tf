data "aws_region" "current" {}

resource "aws_cloudwatch_log_group" "name_microservice" {
    name = "${var.project_name}-name-microservice"
}

data "aws_iam_role" "task_execution" {
    name = var.task_execution_role_name
}

resource "aws_ecs_task_definition" "name_microservice" {
    family = "${var.project_name}-name-microservice"
    execution_role_arn = data.aws_iam_role.task_execution.arn

    requires_compatibilities = ["FARGATE"]

    cpu = 256
    memory = 512
    network_mode = "awsvpc"

    container_definitions = jsonencode([{
        name: "${var.project_name}-name-microservice",
        image: aws_ecr_repository.name_microservice.repository_url,
        cpu: 0,
        essential: true,
        secrets: [
            {
                name: "URI",
                valueFrom: aws_ssm_parameter.db_uri.arn,
            },
        ],

        portMappings: [
            {
                containerPort: 8080,
            },
        ],

        logConfiguration: {
            logDriver: "awslogs",
            options: {
                "awslogs-stream-prefix" : "${var.project_name}-name-microservice",
                "awslogs-region" : data.aws_region.current.name,
                "awslogs-group" : aws_cloudwatch_log_group.name_microservice.name,
            },
        },
    }])

    tags = merge(
        var.common_tags,
        {
            Name: "${var.project_name} Task Definition"
        },
    )
}
