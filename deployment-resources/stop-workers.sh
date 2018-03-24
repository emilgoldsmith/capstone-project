#!/bin/bash

ps -A | grep "ssh\b" | awk '{print $1}' | xargs kill
