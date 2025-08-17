COMPOSE ?= docker compose
GRADLE  ?= ./gradlew
PROFILE ?= dev
REPORT  ?= build/reports/jacoco/test/html/index.html
OS      := $(shell uname)

.PHONY: up down run restart test coverage report help

help:
	@echo "make up       - Sobe Postgres + pgAdmin (docker compose up -d)"
	@echo "make down     - Para serviços (docker compose down)"
	@echo "make run      - Executa a app com Spring profile=$(PROFILE)"
	@echo "make restart  - down + up"
	@echo "make test     - Roda os testes (./gradlew test)"
	@echo "make coverage - Clean + testes + relatório JaCoCo"
	@echo "make report   - Abre (ou indica) o relatório de cobertura"

up:
	$(COMPOSE) up -d

down:
	$(COMPOSE) down

run:
	$(GRADLE) bootRun --args="--spring.profiles.active=$(PROFILE)"

restart: down up

test:
	$(GRADLE) test

coverage:
	$(GRADLE) --stop || true
	rm -rf build/
	$(GRADLE) clean test jacocoTestReport --no-daemon

report:
	@if [ -f "$(REPORT)" ]; then \
	  if [ "$(OS)" = "Darwin" ]; then open "$(REPORT)"; \
	  elif command -v xdg-open >/dev/null 2>&1; then xdg-open "$(REPORT)"; \
	  else echo "Abra: $(REPORT)"; fi; \
	else \
	  echo "Relatório não encontrado. Rode: make coverage"; \
	fi
