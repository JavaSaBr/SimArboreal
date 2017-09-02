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

varying vec2 texCoord;

#ifdef SEPARATE_TEXCOORD
    varying vec2 texCoord2;
    attribute vec2 inTexCoord2;
#endif

varying vec4 Color;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

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

    #ifdef USE_WIND
        // some simple wind
        float windStrength = 0.75;

        // Need to know the model's ground position for noise basis
        // otherwise the tree will warp all over the place and it
        // will look strange as the trunk stretches and shrinks.
        vec4 groundPos = worldMatrix * vec4(0.0, 0.0, 0.0, 1.0);

        // Wind is applied to world space
        vec4 wPos = worldMatrix * modelSpacePos;

        wPos.xyz += calculateWind(groundPos.xyz, wPos.xyz - groundPos.xyz, windStrength);
        gl_Position = g_ViewProjectionMatrix * wPos;
    #else
        gl_Position = TransformWorldViewProjection(modelSpacePos);
    #endif

    texCoord = inTexCoord;

    #ifdef SEPARATE_TEXCOORD
        texCoord2 = inTexCoord2;
    #endif

    wPosition = TransformWorld(modelSpacePos).xyz;
    wNormal = TransformWorldNormal(modelSpaceNorm);

    #if defined(NORMALMAP) || defined(PARALLAXMAP)
        wTangent = vec4(TransformWorldNormal(modelSpaceTan),inTangent.w);
    #endif

    Color = m_BaseColor;

    #ifdef VERTEX_COLOR
        Color *= inColor;
    #endif
}
