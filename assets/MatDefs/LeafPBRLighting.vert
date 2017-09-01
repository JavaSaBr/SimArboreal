#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"
#import "Common/ShaderLib/Skinning.glsllib"

#ifdef USE_WIND
    uniform float g_Time;
#endif

#ifdef INSTANCING
#else
    #define worldMatrix g_WorldMatrix
#endif

#import "MatDefs/TreeWind.glsllib"

uniform vec4 m_BaseColor;
uniform vec4 g_AmbientLightColor;
uniform vec3 g_CameraPosition;

varying vec2 texCoord;

#ifdef SEPARATE_TEXCOORD
    varying vec2 texCoord2;
    attribute vec2 inTexCoord2;
#endif

varying vec4 Color;

attribute vec3 inPosition;
attribute vec4 inTexCoord;
attribute vec3 inNormal;
attribute float inSize;

#ifdef VERTEX_COLOR
    attribute vec4 inColor;
#endif

varying vec3 wNormal;
varying vec3 wPosition;

#if defined(NORMALMAP) || defined(PARALLAXMAP)
    attribute vec4 inTangent;
    varying vec4 wTangent;
#endif

varying float vDistance;

void main() {
	vec4 modelSpacePos = vec4(inPosition, 1.0);
	vec3 modelSpaceNorm = inNormal;

    #if (defined(NORMALMAP) || defined(PARALLAXMAP)) && !defined(VERTEX_LIGHTING)
        vec3 modelSpaceTan = inTangent.xyz;
    #endif

    #ifdef NUM_BONES
        #if defined(NORMALMAP) && !defined(VERTEX_LIGHTING)
            Skinning_Compute(modelSpacePos, modelSpaceNorm, modelSpaceTan);
        #else
            Skinning_Compute(modelSpacePos, modelSpaceNorm);
        #endif
    #endif

    // ** Added sections indicated with '**' comments

    // ** inTexCoord contains the real tex coord for the atlas
    // ** and the corner indicator
    texCoord = inTexCoord.zw;
    vec2 corner = inTexCoord.xy;

    // ** Project the model space position down the normal just
    // ** slightly
    modelSpacePos.xyz += modelSpaceNorm * 0.1;

    // ** #if block for position and normal calculation depending
    // ** on billboard type.
    #ifdef SCREEN_PARALLEL
        // Billboard corners are calculated in straight view
        // space and so will rotated to be parallel to the screen
        // even as the camera turns (which can be unnerving)
        vec3 wvPosition = (g_WorldViewMatrix * modelSpacePos).xyz;
        wvPosition.x += (corner.x - 0.5) * inSize;
        wvPosition.y += (corner.y - 0.5) * inSize;

        vDistance = length(wvPosition);

        gl_Position = g_ProjectionMatrix * vec4(wvPosition, 1.0);

        vec3 wvNormal = normalize(vec3(corner.x - 0.5, corner.y - 0.5, 0.5));
    #else

        // Get the world position (not world view) because
        // billboarding will be done in world space
        vec4 wmPosition = worldMatrix * modelSpacePos;

        #ifdef USE_WIND
            // Calculate the wind from the unprojected position so that
            // the whole leaf quad gets the same wind
            vec4 groundPos = worldMatrix * vec4(0.0, 0.0, 0.0, 1.0);
            float windStrength = 0.75;
            vec3 wind = calculateWind(groundPos.xyz, wmPosition.xyz - groundPos.xyz, windStrength);
            wmPosition.xyz += wind;
        #endif

        // Calculate the screen parallel axis vectors
        vec3 cameraOffset = wmPosition.xyz - g_CameraPosition;
        vDistance = length(cameraOffset);
        vec3 dir = cameraOffset/vDistance; //normalize(wmPosition.xyz - g_CameraPosition);
        vec3 left = normalize(cross(dir, vec3(0.0, 1.0, 0.0)));
        vec3 up = normalize(cross(left, dir));
        vec3 billboardNormal = normalize(cross(left, up));

        // Move the corners out relative to our calculated
        // axes and scaled by inSize
        wmPosition.xyz += left * (corner.x - 0.5) * inSize;
        wmPosition.xyz += up * (corner.y - 0.5) * inSize;
        // Push it a little towards the camera (should maybe be a parameter)
        wmPosition.xyz += billboardNormal * 0.5;

        // Calculate the world view position
        vec3 wvPosition = (g_ViewMatrix * wmPosition).xyz;

        gl_Position = g_ViewProjectionMatrix * wmPosition;

        // Calculate a splayed set of normals based on the corner to simulate
        // curvature.  This allows the billboard to be lit no matter the
        // current direction.
        // Normal is calculated by mixing the real world-normal for the
        // surface with the splayed normal.
        wNormal = (worldMatrix * vec4(modelSpaceNorm, 0.0)).xyz * 0.1;
        wNormal += left * (corner.x - 0.5);
        wNormal += up * (corner.y - 0.5);
        wNormal += billboardNormal * 0.5;

        // Now convert the world normal to world view space
        vec3 wvNormal = normalize((g_ViewMatrix * vec4(wNormal, 0.0)).xyz);
    #endif

    #ifdef SEPARATE_TEXCOORD
        texCoord2 = inTexCoord2;
    #endif

    wPosition = wmPosition.xyz;

    #if defined(NORMALMAP) || defined(PARALLAXMAP)
        wTangent = vec4(TransformWorldNormal(modelSpaceTan),inTangent.w);
    #endif

    Color = m_BaseColor;

    #ifdef VERTEX_COLOR
        Color *= inColor;
    #endif
}
