COMPOSE ?= docker compose
GRADLE  ?= ./gradlew
PROFILE ?= dev

.PHONY: up down run restart help

help:
	@echo "make up       - Sobe Postgres + pgAdmin (docker compose up -d)"
	@echo "make down     - Para serviços (docker compose down)"
	@echo "make run      - Executa a app com Spring profile=$(PROFILE)"
	@echo "make restart  - down + up"

up:
	$(COMPOSE) up -d

down:
	$(COMPOSE) down

run:
	$(GRADLE) bootRun --args="--spring.profiles.active=$(PROFILE)"

restart: down up