from Utils import *

def run_behavior(environment, direction, wall_collision,x,z):
    """
    Executes the chicken's behavior based on its surroundings and the last known direction of movement.
    this Chicken doesn't follow the food, and is afraid of everythings that moves!
    
    :param environment: A 3x3 list representing the chicken's surroundings.
    :param direction: A string representing the direction of movement.
        One of ["N", "NE", "E", "SE", "S", "SW", "W", "NW"].
    :return: A tuple (move_x, move_z, dz, new_direction) representing the movement vector and direction.
    """

    # Check the tile in the current direction and the next tile
    current_tile = get_tile(environment, 1, 1)
    next_tile = get_tile(environment, *direction_offsets[direction])
    
    # If the current direction is blocked, turn around and set wall_collision to True , should not happen!
    if current_tile == "WALL":
        wall_collision = True
        return move_vectors[opposite_directions[direction]] + (direction,) + (wall_collision,)
    
    # If there is an Entity in the way, Turn around.
    if next_tile == "GHOST" or next_tile == "SNACKMAN" or next_tile == "CHICKEN":
        return move_vectors[opposite_directions[direction]] + (direction,) + (wall_collision,)
    
    # if the Ghost or Snackman is on the same tile, freeze.
    if next_tile == "GHOST" or next_tile == "SNACKMAN":
         return (0.0, 0.0, 0.0, direction, wall_collision)
        
    # If the current direction is not blocked or has food in it, continue moving in that direction
    if (next_tile == "FOOD" or next_tile == "FREE") and wall_collision == False:
        return move_vectors[direction] + (direction,) + (wall_collision,)
    
    # If the current direction is blocked, check if an alternative is available
    if wall_collision or (next_tile == "WALL" and ((direction == "N" or direction == "S") and (x % 1.0 > 0.49 and x % 1.0 < 0.51)) or ((direction == "E" or direction == "W") and (z % 1.0 > 0.49 and z % 1.0 < 0.51))):
        valid_directions = []
        for new_direction in alternatives[direction]:
            if get_tile(environment,*direction_offsets[new_direction]) != "WALL" and get_tile(environment,*direction_offsets[new_direction]) != "GHOST":
                valid_directions.append(new_direction)
        if valid_directions:
            new_direction = random.choice(valid_directions)  # Randomly select a valid direction
            wall_collision = False
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
        else:
            wall_collision = False
            return move_vectors[opposite_directions[direction]] + (opposite_directions[direction],) + (wall_collision,)

    # If no alternative is available, stay in place
    return (0.0, 0.0, 0.0, direction, wall_collision)