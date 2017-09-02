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
attribute vec2 inTexCoord;
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

    texCoord = inTexCoord;

    #ifdef SEPARATE_TEXCOORD
        texCoord2 = inTexCoord2;
    #endif

    // *** Determine the positions and normals

    #ifndef SCREENSPACE

        // The default way
        vec4 wmPosition = worldMatrix * modelSpacePos;
        vec3 wmNormal = (worldMatrix * vec4(modelSpaceNorm, 0.0)).xyz;
        vec3 dir = normalize(wmPosition.xyz - g_CameraPosition);

        #ifdef USE_WIND
            float windStrength = 0.75;
            // Need to know the model's ground position for noise basis
            // otherwise the tree will warp all over the place and it
            // will look strange as the trunk stretches and shrinks.
            vec4 groundPos = worldMatrix * vec4(0.0, 0.0, 0.0, 1.0);
            wmPosition.xyz += calculateWind(groundPos.xyz, wmPosition.xyz - groundPos.xyz, windStrength);
        #endif

        vec3 offset = normalize(cross(dir, wmNormal));
        wmPosition.xyz += offset * inSize;

        vec3 wvPosition = (g_ViewMatrix * wmPosition).xyz;

        gl_Position = g_ViewProjectionMatrix * wmPosition;
        wPosition = wmPosition.xyz;
        wNormal = (cross(offset, wmNormal) * 0.05) + (offset * inSize);

    #else
        // Calculate in viewspace... the billboarding will crawl
        // as the camera turns
        vec3 wvPosition = (g_WorldViewMatrix * modelSpacePos).xyz;
        //vec3 wvNormal  = normalize(g_NormalMatrix * modelSpaceNorm);
        vec3 wvNormal = TransformNormal(modelSpaceNorm);

        vec3 offset = normalize(vec3(wvNormal.y, -wvNormal.x, 0.0));
        wvPosition += offset * inSize;

        gl_Position = g_ProjectionMatrix * vec4(wvPosition, 1.0);
        wPosition = wvPosition;

        // Now calculate a splayed normal for this new configuration
        vec3 surfaceNormal = cross(offset, wvNormal);
        //wvNormal = normalize((surfaceNormal * 0.25) + (offset * inSize));
        wvNormal = (surfaceNormal * 0.25) + (offset * inSize);
        //wvNormal = normalize(wvNormal);
        wNormal = wvNormal;
    #endif

    // *** end billboard changes

    #if defined(NORMALMAP) || defined(PARALLAXMAP)
        wTangent = vec4(TransformWorldNormal(modelSpaceTan),inTangent.w);
    #endif

    Color = m_BaseColor;

    #ifdef VERTEX_COLOR
        Color *= inColor;
    #endif
}
