direction_offsets = {
    "NW": (0, 0), "N": (0, 1), "NE": (0, 2), "W" : (1, 0),           
    "E" : (1, 2), "SW": (2, 0), "S": (2, 1), "SE": (2, 2) 
    }
    
move_vectors = {
    "N": (0.05, 0.0, 0.0), "NE": (0.05, 0.0, 0.05),
    "E": (0.0, 0.0, 0.05), "SE": (-0.05, 0.0, 0.05),
    "S": (-0.05, 0.0, 0.0), "SW": (-0.05, 0.0, -0.05),
    "W": (0.0, 0.0, -0.05), "NW": (0.05, 0.0, -0.05)
}
    
alternatives = {
    "N": ["E", "W"], "E": ["N", "S"],
    "S": ["W", "E"], "W": ["S", "N"],
}
    
opposite_directions = {
    "N": "S", "NE": "SW", "E": "W", "SE": "NW",
    "S": "N", "SW": "NE", "W": "E", "NW": "SE"
}

def get_tile(environment, row, col):
    """Get the tile content based on the row, col. """
    if 0 <= row < 3 and 0 <= col < 3:
        return environment[row][col]
    return "OUT"  # Out of bounds