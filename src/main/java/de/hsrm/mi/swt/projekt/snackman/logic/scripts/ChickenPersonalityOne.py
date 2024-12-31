import random

def run_behavior(environment, direction, wall_collision):
    """
    Executes the chicken's behavior based on its surroundings and the last known direction of movement.
    
    :param environment: A 3x3 list representing the chicken's surroundings.
    :param direction: A string representing the direction of movement.
        One of ["N", "NE", "E", "SE", "S", "SW", "W", "NW"].
    :return: A tuple (move_x, move_z, dz, new_direction) representing the movement vector and direction.
    """
    
    direction_offsets = {
    "NW": (0, 0), "N": (0, 1), "NE": (0, 2), "W" : (1, 0),           
    "E" : (1, 2), "SW": (2, 0), "S": (2, 1), "SE": (2, 2) 
    }
    
    move_vectors = {
        "N": (0.1, 0.0, 0.0), "NE": (0.1, 0.0, 0.1),
        "E": (0.0, 0.0, 0.1), "SE": (-0.1, 0.0, 0.1),
        "S": (-0.1, 0.0, 0.0), "SW": (-0.1, 0.0, -0.1),
        "W": (0.0, 0.0, -0.1), "NW": (0.1, 0.0, -0.1)
    }
    
    alternatives = {
        "N": ["E", "W", "S"], "E": ["N", "S", "W"],
        "S": ["W", "E", "N"], "W": ["S", "N", "E"],
    }
    
    opposite_directions = {
        "N": "S", "NE": "SW", "E": "W", "SE": "NW",
        "S": "N", "SW": "NE", "W": "E", "NW": "SE"
    }

    def get_tile(row, col):
        """Get the tile content based on the row, col. """
        if 0 <= row < 3 and 0 <= col < 3:
            return environment[row][col]
        return "NULL"  # Out of bounds

    # Check the tile in the current direction and the next Tile
    current_tile = get_tile(1, 1)
    next_tile = get_tile(*direction_offsets[direction])

    # If the current direction is not blocked, continue moving in that direction
    if current_tile != "WALL" and wall_collision == False: 
        return move_vectors[direction] + (direction,) + (wall_collision,)
    
    # If the current direction is blocked, turn around and set wall_collision to True
    if current_tile == "WALL":
        wall_collision = True
        return move_vectors[opposite_directions[direction]] + (direction,) + (wall_collision,)

    # If the current direction is blocked, check if an alternative is available
    if wall_collision == True:
        for new_direction in alternatives[direction]:
            current_tile = get_tile(*direction_offsets[new_direction])
            if current_tile != "WALL":
                wall_collision = False
                return move_vectors[new_direction] + (new_direction,) + (wall_collision,)

    # If no alternative is available, stay in place
    return (0.0, 0.0, 0.0, direction, wall_collision)