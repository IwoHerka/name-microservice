data "aws_ecs_cluster" "kopytko" {
  cluster_name = var.ecs_cluster
}

resource "aws_ecs_service" "name_microservice" {
  name            = "${var.project_name}-name-microservice"
  cluster         = data.aws_ecs_cluster.kopytko.id
  desired_count   = 1
  task_definition = aws_ecs_task_definition.name_microservice.arn

  network_configuration {
    subnets          = var.subnet_ids
    security_groups  = var.security_group_ids
    assign_public_ip = true
  }

  launch_type = "FARGATE"

  deployment_controller {
    type = "ECS"
  }
}
