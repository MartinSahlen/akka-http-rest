#!/bin/bash

createdb blog
psql -d blog -f bin/db.sql
