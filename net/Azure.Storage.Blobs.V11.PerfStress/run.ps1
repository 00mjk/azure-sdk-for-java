#!/usr/bin/env pwsh

docker run -it --rm --network host -e STORAGE_CONNECTION_STRING azure-storage-blobs-v11-perfstress-net @args
