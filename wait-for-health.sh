#!/bin/bash

echo "Waiting for backend health..."
until curl -s http://localhost:8080/api/actuator/health | grep '"status":"UP"'; do
  sleep 5
done

echo "Backend is healthy!"
