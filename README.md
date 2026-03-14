# Life Collage

## Description

Life Collage is an application where users can build their own safe place by adding good memories that bring them joy. All the elements are shown on the user's main board, where they can open each one to see the details, modify them or add new ones. THe goal of the application is to give people a collage they can revisit when life gets tough, a reminder of all the good aspects of life.
The project is made for Android devices, but it also has a Flutter UI implemented.

## CRUD operations

The elements can be viewed on a homepage, all of them in a big scrollable collage.
The user can add a new element by providing the details and saving it. It has checks for the mandatory fields.
When the user clicks an element they can choose to edit or delete it. When they choose to delete a popup appears asking for confirmation. If the user chooses to modify the element, a form appears where the details can be changed, saved and after this they will appear on the main page.

## Persistence

The app supports data persistence both locally and on the server.
Local database: stores user data for offline access and CRUD operations (Create, Read, Update, Delete).
Server database: syncs the same entities when online to ensure data backup and cross-device availability.

Persisted operations:
- Create new entries
- Update existing entries
- Delete entries

## Offline Behaviour
When the device is offline, all CRUD operations are handled locally.
Each operation is synchronized with the server once the device reconnects.

Offline scenarios:
- Create: new entries (e.g., a memory or dream) are saved locally and marked as pending sync until the connection is restored.
- Read: existing local data remains accessible, allowing full offline browsing of all previously saved items.
- Update: changes made while offline are applied locally and queued for synchronization once online.
- Delete: deleted items are flagged locally and removed from the server upon reconnection.

