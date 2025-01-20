from Utils import *
from java.util import Random
rand = Random()

def run_behavior(environment, direction, wall_collision, x, z, y):
    """
    Executes the chicken's dumb behavior based on its surroundings.
    This chicken runs towards ghosts, avoids food, and jumps when seeing other chickens.

    :param environment: A 3x3 list representing the chicken's surroundings.
    :param direction: A string representing the direction of movement.
        One of ["N", "NE", "E", "SE", "S", "SW", "W", "NW"].
    :return: A tuple (move_x, move_z, move_y, new_direction, wall_collision) representing the movement vector and direction.
    """

    # Check the current and next tile
    current_tile = get_tile(environment, 1, 1)
    next_tile = get_tile(environment, *direction_offsets[direction])

    # make sure to fall after jump 
    if y > 0:
        return move_vectors["FALL"] + (direction,) + (wall_collision,)
    
    if current_tile == "WALL":
        wall_collision = True
        new_direction = opposite_directions[direction]
        return move_vectors[new_direction] + (new_direction,) + (wall_collision,)
    
    # check if there is food in the current direction and avoid it
    if next_tile == "FOOD":
        valid_directions = [new_direction for new_direction in alternatives[direction]
                            if get_tile(environment, *direction_offsets[new_direction]) != "FOOD"]

        if valid_directions:
            new_direction = valid_directions[int(rand.nextInt(len(valid_directions)))]
            return move_vectors[new_direction] + (new_direction,) + (wall_collision,)

        new_direction = opposite_directions[direction]
        return move_vectors[new_direction] + (new_direction,) + (wall_collision,)

    # "Chase" ghosts
    # if a ghost is found in the surrondings, move towards it 
    for dir_key, offset in direction_offsets.items():
            tile = get_tile(environment, *offset)
            if tile == "GHOST":
                new_direction = dir_key
                return move_vectors[new_direction] + (new_direction,) + (False,)

    # If chicken sees another chicken, it jumps happy
    if next_tile == "CHICKEN":
        return move_vectors["JUMP"] + (direction,) + (wall_collision,)

    # If there is a wall, choose a random valid direction
    if wall_collision or (next_tile == "WALL" and 
                          ((direction in ["N", "S"] and 0.49 < x % 1.0 < 0.51) 
                           or (direction in ["E", "W"] and 0.49 < z % 1.0 < 0.51) 
                           or (direction in ["NW", "NE", "SW", "SE"] and 0.49 < x % 1.0 < 0.51 and 0.49 < z % 1.0 < 0.51)
                          )):
        valid_directions = []
        
         # Simplify the direction if it's diagonal (NW, NE, SW, SE)
        if direction in ["NW", "NE", "SW", "SE"]:
            # Simplify to the main direction
            direction = {"NW": "N", "NE": "N", "SW": "S", "SE": "S"}[direction]

        for new_direction in alternatives[direction]:
            if get_tile(environment,*direction_offsets[new_direction]) != "WALL":
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
        

    # If nothing special, continue in the current direction
    return move_vectors[direction] + (direction,) + (wall_collision,)
