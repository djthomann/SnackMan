from Utils import *

from java.util import Random
rand = Random()

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
    
    # If the current direction is blocked, turn around and set wall_collision to True
    if current_tile == "WALL":
        wall_collision = True
        new_direction = opposite_directions[direction]
        return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
    
    # If there is an Entity in the way, Turn around.
    if next_tile == "GHOST" or next_tile == "SNACKMAN" or next_tile == "CHICKEN":
        valid_directions = []
        
        valid_directions.append(opposite_directions[direction])
        for new_direction in alternatives[direction]:
            if get_tile(environment,*direction_offsets[new_direction]) != "WALL" and get_tile(environment,*direction_offsets[new_direction]) != "GHOST" and get_tile(environment,*direction_offsets[new_direction]) != "SNACKMAN" and get_tile(environment,*direction_offsets[new_direction]) != "CHICKEN":
                valid_directions.append(new_direction)

        if len(valid_directions) == 1:
            new_direction = valid_directions[0]
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
        
        elif len(valid_directions) > 1:
            new_direction = valid_directions[int(rand.nextInt(len(valid_directions)))]
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
    
    # if the Ghost or Snackman is on the same tile, freeze.
    if current_tile == "GHOST" or current_tile == "SNACKMAN":
         return (0.0, 0.0, 0.0, direction, wall_collision)
    
    # If the current direction is blocked, check if an alternative is available
    if wall_collision or (next_tile == "WALL" and ((direction in ["N", "S"] and 0.49 < x % 1.0 < 0.51) or (direction in ["E", "W"] and 0.49 < z % 1.0 < 0.51))):
        valid_directions = []

        for new_direction in alternatives[direction]:
            if get_tile(environment,*direction_offsets[new_direction]) != "WALL" and get_tile(environment,*direction_offsets[new_direction]) != "GHOST":
                valid_directions.append(new_direction)

        if len(valid_directions) > 1:
            wall_collision = False
            new_direction = valid_directions[int(rand.nextInt(len(valid_directions)))]
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
        
        if len(valid_directions) == 1:
            wall_collision = False
            new_direction = valid_directions[0]
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
        
        else:
            wall_collision = False
            new_direction = opposite_directions[direction]
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)

    # no reason to do any action, continue moving in that direction
    return move_vectors[direction] + (direction,) + (wall_collision,)    
