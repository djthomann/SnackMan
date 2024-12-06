import random

def run_behavior(environment, last_direction=(1, 0)):
    """
    Executes the chicken's behavior based on its surroundings and the last known direction of movement.
    
    :param environment: A hashmap (Dictionary) representing the surroundings. Each key is a string like "tile_row_col"
                        and each value is a string representing the type of tile ("FREE", "ITEM", "WALL", "NULL").
    :param last_direction: A tuple (directionX, directionY) representing the last direction of movement.
                           Example: (1, 0) means moving to the right.
    :return: A tuple (directionX, directionY, dz) representing the movement direction.
             Example: (1, 0, 0) means moving one tile to the right.
    """
    
    # Helper function to get the tile at a specific row, col relative to the chicken's position
    def get_tile(row, col):
        key = f"tile_{row}_{col}"  # Key format from the hashmap
        return environment.get(key, "NULL")  # Return "NULL" if key doesn't exist
    
    # Scan the environment for "ITEM" (food)
    for directionY in range(-1, 2):
        for directionX in range(-1, 2):
            # Skip the center tile (where the chicken currently is)
            if directionX == 0 and directionY == 0:
                continue

            tile = get_tile(directionX, directionY)
            if tile == "ITEM":
                # Move towards the food
                return directionX, directionY, 0

    # Continue in the same direction unless blocked by a wall
    forward_tile = get_tile(last_direction[0],last_direction[1])
    if forward_tile != "WALL":
        # No wall, continue in the same direction
        return last_direction[0], last_direction[1], 0

    # If forward is blocked, try to rotate 90 degrees (left or right)
    right_direction = (last_direction[1], -last_direction[0])  # 90 degrees right turn
    left_direction = (-last_direction[1], last_direction[0])  # 90 degrees left turn

    # Check if right is free
    if get_tile(right_direction[1] , right_direction[0]) != "WALL":
        return right_direction[1], right_direction[0], 0

    # Check if left is free
    if get_tile(left_direction[1] , left_direction[0] ) != "WALL":
        return left_direction[1], left_direction[0], 0

    # If both right and left are blocked, turn around (180-degree turn)
    reverse_direction = (-last_direction[1], -last_direction[0])  # Reverse the direction
    
    return reverse_direction[1], reverse_direction[0], 0