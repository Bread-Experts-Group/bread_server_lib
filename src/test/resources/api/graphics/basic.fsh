#version 330 core

in vec4 vColor;
in vec2 vTexCoord;

out vec4 FragColor;

uniform sampler2D tTexture;

void main() {
    FragColor = texture(tTexture, vTexCoord);
}