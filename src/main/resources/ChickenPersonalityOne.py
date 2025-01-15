from Utils import *
from java.util import Random
rand = Random()

def run_behavior(environment, direction, wall_collision,x,z):
    """
    Executes the chicken's behavior based on its surroundings and the last known direction of movement.
    this Chicken looks for food, and is afraid of Ghosts only.
    
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
        new_direction = opposite_directions[direction]
        return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
    
    # If there is a ghost in the way, Turn around.
    if next_tile == "GHOST":
        new_direction = opposite_directions[direction]
        return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
    
    # if the Ghost is on the same tile, freeze.
    if  current_tile == "GHOST":
         return (0.0, 0.0, 0.0, direction, wall_collision)

    # If the current direction is not blocked and has no food in it, checks for food right or left
    if next_tile != "FOOD" and wall_collision == False:
        if ((direction == "N" or direction == "S") and (x % 1.0 > 0.49 and x % 1.0 < 0.51)) or ((direction == "E" or direction == "W") and (z % 1.0 > 0.49 and z % 1.0 < 0.51)):
            valid_directions = []
            for new_direction in alternatives[direction]:
                if get_tile(environment,*direction_offsets[new_direction]) == "FOOD":
                    valid_directions.append(new_direction)
            if len(valid_directions) > 1:
                new_direction = valid_directions[int(rand.nextInt(2))]  # Randomly select a valid direction
                wall_collision = False
                return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
            if len(valid_directions) == 1:
                new_direction = valid_directions[0]  # Randomly select a valid direction
                wall_collision = False
                return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
            else:
                return move_vectors[direction] + (direction,) + (wall_collision,)
    
    # If the current direction is blocked, check if an alternative is available
    if wall_collision or (next_tile == "WALL" and ((direction == "N" or direction == "S") and (x % 1.0 > 0.49 and x % 1.0 < 0.51)) or ((direction == "E" or direction == "W") and (z % 1.0 > 0.49 and z % 1.0 < 0.51))):
        valid_directions = []
        for new_direction in alternatives[direction]:
            if get_tile(environment,*direction_offsets[new_direction]) != "WALL" and get_tile(environment,*direction_offsets[new_direction]) != "GHOST":
                valid_directions.append(new_direction)
        if len(valid_directions) > 1:
            new_direction = valid_directions[int(rand.nextInt(2))]  # Randomly select a valid direction
            wall_collision = False
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
        if len(valid_directions) == 1:
            new_direction = valid_directions[0]  # Randomly select a valid direction
            wall_collision = False
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
        else:
            wall_collision = False
            new_direction = opposite_directions[direction]
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
        
    # no reason to do any action, continue moving in that direction
    return move_vectors[direction] + (direction,) + (wall_collision,)