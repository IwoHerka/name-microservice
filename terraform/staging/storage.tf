resource "aws_ssm_parameter" "db_uri" {
    name = "${var.project_name}-db-password"
    description = "DB URI for Planning Bot Name Microservice"
    type = "SecureString"
    value = "mongodb://${var.db_host}:${var.db_port}/pb-name-microservice"
}
