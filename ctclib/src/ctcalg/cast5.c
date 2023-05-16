/* cast5.c
**
**  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> December 1997
**  All rights reserved.  For full licence details see file licences.c
**
**  The CAST-5 cypher as defined in RFC 2144
*/

#include "cast5.h"
#include "utils.h"
#include <string.h>
#ifdef TEST
#include <stdio.h>
#endif

#define SBOXLEN 256
#define KEYSIZE 16
#define SUBKEYS 32
/* S-Boxes */

static uint32_t S1[SBOXLEN] = {
    0x30fb40d4L, 0x9fa0ff0bL, 0x6beccd2fL, 0x3f258c7aL,
    0x1e213f2fL, 0x9c004dd3L, 0x6003e540L, 0xcf9fc949L,
    0xbfd4af27L, 0x88bbbdb5L, 0xe2034090L, 0x98d09675L,
    0x6e63a0e0L, 0x15c361d2L, 0xc2e7661dL, 0x22d4ff8eL,
    0x28683b6fL, 0xc07fd059L, 0xff2379c8L, 0x775f50e2L,
    0x43c340d3L, 0xdf2f8656L, 0x887ca41aL, 0xa2d2bd2dL,
    0xa1c9e0d6L, 0x346c4819L, 0x61b76d87L, 0x22540f2fL,
    0x2abe32e1L, 0xaa54166bL, 0x22568e3aL, 0xa2d341d0L,
    0x66db40c8L, 0xa784392fL, 0x004dff2fL, 0x2db9d2deL,
    0x97943facL, 0x4a97c1d8L, 0x527644b7L, 0xb5f437a7L,
    0xb82cbaefL, 0xd751d159L, 0x6ff7f0edL, 0x5a097a1fL,
    0x827b68d0L, 0x90ecf52eL, 0x22b0c054L, 0xbc8e5935L,
    0x4b6d2f7fL, 0x50bb64a2L, 0xd2664910L, 0xbee5812dL,
    0xb7332290L, 0xe93b159fL, 0xb48ee411L, 0x4bff345dL,
    0xfd45c240L, 0xad31973fL, 0xc4f6d02eL, 0x55fc8165L,
    0xd5b1caadL, 0xa1ac2daeL, 0xa2d4b76dL, 0xc19b0c50L,
    0x882240f2L, 0x0c6e4f38L, 0xa4e4bfd7L, 0x4f5ba272L,
    0x564c1d2fL, 0xc59c5319L, 0xb949e354L, 0xb04669feL,
    0xb1b6ab8aL, 0xc71358ddL, 0x6385c545L, 0x110f935dL,
    0x57538ad5L, 0x6a390493L, 0xe63d37e0L, 0x2a54f6b3L,
    0x3a787d5fL, 0x6276a0b5L, 0x19a6fcdfL, 0x7a42206aL,
    0x29f9d4d5L, 0xf61b1891L, 0xbb72275eL, 0xaa508167L,
    0x38901091L, 0xc6b505ebL, 0x84c7cb8cL, 0x2ad75a0fL,
    0x874a1427L, 0xa2d1936bL, 0x2ad286afL, 0xaa56d291L,
    0xd7894360L, 0x425c750dL, 0x93b39e26L, 0x187184c9L,
    0x6c00b32dL, 0x73e2bb14L, 0xa0bebc3cL, 0x54623779L,
    0x64459eabL, 0x3f328b82L, 0x7718cf82L, 0x59a2cea6L,
    0x04ee002eL, 0x89fe78e6L, 0x3fab0950L, 0x325ff6c2L,
    0x81383f05L, 0x6963c5c8L, 0x76cb5ad6L, 0xd49974c9L,
    0xca180dcfL, 0x380782d5L, 0xc7fa5cf6L, 0x8ac31511L,
    0x35e79e13L, 0x47da91d0L, 0xf40f9086L, 0xa7e2419eL,
    0x31366241L, 0x051ef495L, 0xaa573b04L, 0x4a805d8dL,
    0x548300d0L, 0x00322a3cL, 0xbf64cddfL, 0xba57a68eL,
    0x75c6372bL, 0x50afd341L, 0xa7c13275L, 0x915a0bf5L,
    0x6b54bfabL, 0x2b0b1426L, 0xab4cc9d7L, 0x449ccd82L,
    0xf7fbf265L, 0xab85c5f3L, 0x1b55db94L, 0xaad4e324L,
    0xcfa4bd3fL, 0x2deaa3e2L, 0x9e204d02L, 0xc8bd25acL,
    0xeadf55b3L, 0xd5bd9e98L, 0xe31231b2L, 0x2ad5ad6cL,
    0x954329deL, 0xadbe4528L, 0xd8710f69L, 0xaa51c90fL,
    0xaa786bf6L, 0x22513f1eL, 0xaa51a79bL, 0x2ad344ccL,
    0x7b5a41f0L, 0xd37cfbadL, 0x1b069505L, 0x41ece491L,
    0xb4c332e6L, 0x032268d4L, 0xc9600accL, 0xce387e6dL,
    0xbf6bb16cL, 0x6a70fb78L, 0x0d03d9c9L, 0xd4df39deL,
    0xe01063daL, 0x4736f464L, 0x5ad328d8L, 0xb347cc96L,
    0x75bb0fc3L, 0x98511bfbL, 0x4ffbcc35L, 0xb58bcf6aL,
    0xe11f0abcL, 0xbfc5fe4aL, 0xa70aec10L, 0xac39570aL,
    0x3f04442fL, 0x6188b153L, 0xe0397a2eL, 0x5727cb79L,
    0x9ceb418fL, 0x1cacd68dL, 0x2ad37c96L, 0x0175cb9dL,
    0xc69dff09L, 0xc75b65f0L, 0xd9db40d8L, 0xec0e7779L,
    0x4744ead4L, 0xb11c3274L, 0xdd24cb9eL, 0x7e1c54bdL,
    0xf01144f9L, 0xd2240eb1L, 0x9675b3fdL, 0xa3ac3755L,
    0xd47c27afL, 0x51c85f4dL, 0x56907596L, 0xa5bb15e6L,
    0x580304f0L, 0xca042cf1L, 0x011a37eaL, 0x8dbfaadbL,
    0x35ba3e4aL, 0x3526ffa0L, 0xc37b4d09L, 0xbc306ed9L,
    0x98a52666L, 0x5648f725L, 0xff5e569dL, 0x0ced63d0L,
    0x7c63b2cfL, 0x700b45e1L, 0xd5ea50f1L, 0x85a92872L,
    0xaf1fbda7L, 0xd4234870L, 0xa7870bf3L, 0x2d3b4d79L,
    0x42e04198L, 0x0cd0ede7L, 0x26470db8L, 0xf881814cL,
    0x474d6ad7L, 0x7c0c5e5cL, 0xd1231959L, 0x381b7298L,
    0xf5d2f4dbL, 0xab838653L, 0x6e2f1e23L, 0x83719c9eL,
    0xbd91e046L, 0x9a56456eL, 0xdc39200cL, 0x20c8c571L,
    0x962bda1cL, 0xe1e696ffL, 0xb141ab08L, 0x7cca89b9L,
    0x1a69e783L, 0x02cc4843L, 0xa2f7c579L, 0x429ef47dL,
    0x427b169cL, 0x5ac9f049L, 0xdd8f0f00L, 0x5c8165bfL,
};

static uint32_t S2[SBOXLEN] = {
    0x1f201094L, 0xef0ba75bL, 0x69e3cf7eL, 0x393f4380L,
    0xfe61cf7aL, 0xeec5207aL, 0x55889c94L, 0x72fc0651L,
    0xada7ef79L, 0x4e1d7235L, 0xd55a63ceL, 0xde0436baL,
    0x99c430efL, 0x5f0c0794L, 0x18dcdb7dL, 0xa1d6eff3L,
    0xa0b52f7bL, 0x59e83605L, 0xee15b094L, 0xe9ffd909L,
    0xdc440086L, 0xef944459L, 0xba83ccb3L, 0xe0c3cdfbL,
    0xd1da4181L, 0x3b092ab1L, 0xf997f1c1L, 0xa5e6cf7bL,
    0x01420ddbL, 0xe4e7ef5bL, 0x25a1ff41L, 0xe180f806L,
    0x1fc41080L, 0x179bee7aL, 0xd37ac6a9L, 0xfe5830a4L,
    0x98de8b7fL, 0x77e83f4eL, 0x79929269L, 0x24fa9f7bL,
    0xe113c85bL, 0xacc40083L, 0xd7503525L, 0xf7ea615fL,
    0x62143154L, 0x0d554b63L, 0x5d681121L, 0xc866c359L,
    0x3d63cf73L, 0xcee234c0L, 0xd4d87e87L, 0x5c672b21L,
    0x071f6181L, 0x39f7627fL, 0x361e3084L, 0xe4eb573bL,
    0x602f64a4L, 0xd63acd9cL, 0x1bbc4635L, 0x9e81032dL,
    0x2701f50cL, 0x99847ab4L, 0xa0e3df79L, 0xba6cf38cL,
    0x10843094L, 0x2537a95eL, 0xf46f6ffeL, 0xa1ff3b1fL,
    0x208cfb6aL, 0x8f458c74L, 0xd9e0a227L, 0x4ec73a34L,
    0xfc884f69L, 0x3e4de8dfL, 0xef0e0088L, 0x3559648dL,
    0x8a45388cL, 0x1d804366L, 0x721d9bfdL, 0xa58684bbL,
    0xe8256333L, 0x844e8212L, 0x128d8098L, 0xfed33fb4L,
    0xce280ae1L, 0x27e19ba5L, 0xd5a6c252L, 0xe49754bdL,
    0xc5d655ddL, 0xeb667064L, 0x77840b4dL, 0xa1b6a801L,
    0x84db26a9L, 0xe0b56714L, 0x21f043b7L, 0xe5d05860L,
    0x54f03084L, 0x066ff472L, 0xa31aa153L, 0xdadc4755L,
    0xb5625dbfL, 0x68561be6L, 0x83ca6b94L, 0x2d6ed23bL,
    0xeccf01dbL, 0xa6d3d0baL, 0xb6803d5cL, 0xaf77a709L,
    0x33b4a34cL, 0x397bc8d6L, 0x5ee22b95L, 0x5f0e5304L,
    0x81ed6f61L, 0x20e74364L, 0xb45e1378L, 0xde18639bL,
    0x881ca122L, 0xb96726d1L, 0x8049a7e8L, 0x22b7da7bL,
    0x5e552d25L, 0x5272d237L, 0x79d2951cL, 0xc60d894cL,
    0x488cb402L, 0x1ba4fe5bL, 0xa4b09f6bL, 0x1ca815cfL,
    0xa20c3005L, 0x8871df63L, 0xb9de2fcbL, 0x0cc6c9e9L,
    0x0beeff53L, 0xe3214517L, 0xb4542835L, 0x9f63293cL,
    0xee41e729L, 0x6e1d2d7cL, 0x50045286L, 0x1e6685f3L,
    0xf33401c6L, 0x30a22c95L, 0x31a70850L, 0x60930f13L,
    0x73f98417L, 0xa1269859L, 0xec645c44L, 0x52c877a9L,
    0xcdff33a6L, 0xa02b1741L, 0x7cbad9a2L, 0x2180036fL,
    0x50d99c08L, 0xcb3f4861L, 0xc26bd765L, 0x64a3f6abL,
    0x80342676L, 0x25a75e7bL, 0xe4e6d1fcL, 0x20c710e6L,
    0xcdf0b680L, 0x17844d3bL, 0x31eef84dL, 0x7e0824e4L,
    0x2ccb49ebL, 0x846a3baeL, 0x8ff77888L, 0xee5d60f6L,
    0x7af75673L, 0x2fdd5cdbL, 0xa11631c1L, 0x30f66f43L,
    0xb3faec54L, 0x157fd7faL, 0xef8579ccL, 0xd152de58L,
    0xdb2ffd5eL, 0x8f32ce19L, 0x306af97aL, 0x02f03ef8L,
    0x99319ad5L, 0xc242fa0fL, 0xa7e3ebb0L, 0xc68e4906L,
    0xb8da230cL, 0x80823028L, 0xdcdef3c8L, 0xd35fb171L,
    0x088a1bc8L, 0xbec0c560L, 0x61a3c9e8L, 0xbca8f54dL,
    0xc72feffaL, 0x22822e99L, 0x82c570b4L, 0xd8d94e89L,
    0x8b1c34bcL, 0x301e16e6L, 0x273be979L, 0xb0ffeaa6L,
    0x61d9b8c6L, 0x00b24869L, 0xb7ffce3fL, 0x08dc283bL,
    0x43daf65aL, 0xf7e19798L, 0x7619b72fL, 0x8f1c9ba4L,
    0xdc8637a0L, 0x16a7d3b1L, 0x9fc393b7L, 0xa7136eebL,
    0xc6bcc63eL, 0x1a513742L, 0xef6828bcL, 0x520365d6L,
    0x2d6a77abL, 0x3527ed4bL, 0x821fd216L, 0x095c6e2eL,
    0xdb92f2fbL, 0x5eea29cbL, 0x145892f5L, 0x91584f7fL,
    0x5483697bL, 0x2667a8ccL, 0x85196048L, 0x8c4baceaL,
    0x833860d4L, 0x0d23e0f9L, 0x6c387e8aL, 0x0ae6d249L,
    0xb284600cL, 0xd835731dL, 0xdcb1c647L, 0xac4c56eaL,
    0x3ebd81b3L, 0x230eabb0L, 0x6438bc87L, 0xf0b5b1faL,
    0x8f5ea2b3L, 0xfc184642L, 0x0a036b7aL, 0x4fb089bdL,
    0x649da589L, 0xa345415eL, 0x5c038323L, 0x3e5d3bb9L,
    0x43d79572L, 0x7e6dd07cL, 0x06dfdf1eL, 0x6c6cc4efL,
    0x7160a539L, 0x73bfbe70L, 0x83877605L, 0x4523ecf1L, 
};

static uint32_t S3[SBOXLEN] = {
    0x8defc240L, 0x25fa5d9fL, 0xeb903dbfL, 0xe810c907L,
    0x47607fffL, 0x369fe44bL, 0x8c1fc644L, 0xaececa90L,
    0xbeb1f9bfL, 0xeefbcaeaL, 0xe8cf1950L, 0x51df07aeL,
    0x920e8806L, 0xf0ad0548L, 0xe13c8d83L, 0x927010d5L,
    0x11107d9fL, 0x07647db9L, 0xb2e3e4d4L, 0x3d4f285eL,
    0xb9afa820L, 0xfade82e0L, 0xa067268bL, 0x8272792eL,
    0x553fb2c0L, 0x489ae22bL, 0xd4ef9794L, 0x125e3fbcL,
    0x21fffceeL, 0x825b1bfdL, 0x9255c5edL, 0x1257a240L,
    0x4e1a8302L, 0xbae07fffL, 0x528246e7L, 0x8e57140eL,
    0x3373f7bfL, 0x8c9f8188L, 0xa6fc4ee8L, 0xc982b5a5L,
    0xa8c01db7L, 0x579fc264L, 0x67094f31L, 0xf2bd3f5fL,
    0x40fff7c1L, 0x1fb78dfcL, 0x8e6bd2c1L, 0x437be59bL,
    0x99b03dbfL, 0xb5dbc64bL, 0x638dc0e6L, 0x55819d99L,
    0xa197c81cL, 0x4a012d6eL, 0xc5884a28L, 0xccc36f71L,
    0xb843c213L, 0x6c0743f1L, 0x8309893cL, 0x0feddd5fL,
    0x2f7fe850L, 0xd7c07f7eL, 0x02507fbfL, 0x5afb9a04L,
    0xa747d2d0L, 0x1651192eL, 0xaf70bf3eL, 0x58c31380L,
    0x5f98302eL, 0x727cc3c4L, 0x0a0fb402L, 0x0f7fef82L,
    0x8c96fdadL, 0x5d2c2aaeL, 0x8ee99a49L, 0x50da88b8L,
    0x8427f4a0L, 0x1eac5790L, 0x796fb449L, 0x8252dc15L,
    0xefbd7d9bL, 0xa672597dL, 0xada840d8L, 0x45f54504L,
    0xfa5d7403L, 0xe83ec305L, 0x4f91751aL, 0x925669c2L,
    0x23efe941L, 0xa903f12eL, 0x60270df2L, 0x0276e4b6L,
    0x94fd6574L, 0x927985b2L, 0x8276dbcbL, 0x02778176L,
    0xf8af918dL, 0x4e48f79eL, 0x8f616ddfL, 0xe29d840eL,
    0x842f7d83L, 0x340ce5c8L, 0x96bbb682L, 0x93b4b148L,
    0xef303cabL, 0x984faf28L, 0x779faf9bL, 0x92dc560dL,
    0x224d1e20L, 0x8437aa88L, 0x7d29dc96L, 0x2756d3dcL,
    0x8b907ceeL, 0xb51fd240L, 0xe7c07ce3L, 0xe566b4a1L,
    0xc3e9615eL, 0x3cf8209dL, 0x6094d1e3L, 0xcd9ca341L,
    0x5c76460eL, 0x00ea983bL, 0xd4d67881L, 0xfd47572cL,
    0xf76cedd9L, 0xbda8229cL, 0x127dadaaL, 0x438a074eL,
    0x1f97c090L, 0x081bdb8aL, 0x93a07ebeL, 0xb938ca15L,
    0x97b03cffL, 0x3dc2c0f8L, 0x8d1ab2ecL, 0x64380e51L,
    0x68cc7bfbL, 0xd90f2788L, 0x12490181L, 0x5de5ffd4L,
    0xdd7ef86aL, 0x76a2e214L, 0xb9a40368L, 0x925d958fL,
    0x4b39fffaL, 0xba39aee9L, 0xa4ffd30bL, 0xfaf7933bL,
    0x6d498623L, 0x193cbcfaL, 0x27627545L, 0x825cf47aL,
    0x61bd8ba0L, 0xd11e42d1L, 0xcead04f4L, 0x127ea392L,
    0x10428db7L, 0x8272a972L, 0x9270c4a8L, 0x127de50bL,
    0x285ba1c8L, 0x3c62f44fL, 0x35c0eaa5L, 0xe805d231L,
    0x428929fbL, 0xb4fcdf82L, 0x4fb66a53L, 0x0e7dc15bL,
    0x1f081fabL, 0x108618aeL, 0xfcfd086dL, 0xf9ff2889L,
    0x694bcc11L, 0x236a5caeL, 0x12deca4dL, 0x2c3f8cc5L,
    0xd2d02dfeL, 0xf8ef5896L, 0xe4cf52daL, 0x95155b67L,
    0x494a488cL, 0xb9b6a80cL, 0x5c8f82bcL, 0x89d36b45L,
    0x3a609437L, 0xec00c9a9L, 0x44715253L, 0x0a874b49L,
    0xd773bc40L, 0x7c34671cL, 0x02717ef6L, 0x4feb5536L,
    0xa2d02fffL, 0xd2bf60c4L, 0xd43f03c0L, 0x50b4ef6dL,
    0x07478cd1L, 0x006e1888L, 0xa2e53f55L, 0xb9e6d4bcL,
    0xa2048016L, 0x97573833L, 0xd7207d67L, 0xde0f8f3dL,
    0x72f87b33L, 0xabcc4f33L, 0x7688c55dL, 0x7b00a6b0L,
    0x947b0001L, 0x570075d2L, 0xf9bb88f8L, 0x8942019eL,
    0x4264a5ffL, 0x856302e0L, 0x72dbd92bL, 0xee971b69L,
    0x6ea22fdeL, 0x5f08ae2bL, 0xaf7a616dL, 0xe5c98767L,
    0xcf1febd2L, 0x61efc8c2L, 0xf1ac2571L, 0xcc8239c2L,
    0x67214cb8L, 0xb1e583d1L, 0xb7dc3e62L, 0x7f10bdceL,
    0xf90a5c38L, 0x0ff0443dL, 0x606e6dc6L, 0x60543a49L,
    0x5727c148L, 0x2be98a1dL, 0x8ab41738L, 0x20e1be24L,
    0xaf96da0fL, 0x68458425L, 0x99833be5L, 0x600d457dL,
    0x282f9350L, 0x8334b362L, 0xd91d1120L, 0x2b6d8da0L,
    0x642b1e31L, 0x9c305a00L, 0x52bce688L, 0x1b03588aL,
    0xf7baefd5L, 0x4142ed9cL, 0xa4315c11L, 0x83323ec5L,
    0xdfef4636L, 0xa133c501L, 0xe9d3531cL, 0xee353783L,
};

static uint32_t S4[SBOXLEN] = {
    0x9db30420L, 0x1fb6e9deL, 0xa7be7befL, 0xd273a298L,
    0x4a4f7bdbL, 0x64ad8c57L, 0x85510443L, 0xfa020ed1L,
    0x7e287affL, 0xe60fb663L, 0x095f35a1L, 0x79ebf120L,
    0xfd059d43L, 0x6497b7b1L, 0xf3641f63L, 0x241e4adfL,
    0x28147f5fL, 0x4fa2b8cdL, 0xc9430040L, 0x0cc32220L,
    0xfdd30b30L, 0xc0a5374fL, 0x1d2d00d9L, 0x24147b15L,
    0xee4d111aL, 0x0fca5167L, 0x71ff904cL, 0x2d195ffeL,
    0x1a05645fL, 0x0c13fefeL, 0x081b08caL, 0x05170121L,
    0x80530100L, 0xe83e5efeL, 0xac9af4f8L, 0x7fe72701L,
    0xd2b8ee5fL, 0x06df4261L, 0xbb9e9b8aL, 0x7293ea25L,
    0xce84ffdfL, 0xf5718801L, 0x3dd64b04L, 0xa26f263bL,
    0x7ed48400L, 0x547eebe6L, 0x446d4ca0L, 0x6cf3d6f5L,
    0x2649abdfL, 0xaea0c7f5L, 0x36338cc1L, 0x503f7e93L,
    0xd3772061L, 0x11b638e1L, 0x72500e03L, 0xf80eb2bbL,
    0xabe0502eL, 0xec8d77deL, 0x57971e81L, 0xe14f6746L,
    0xc9335400L, 0x6920318fL, 0x081dbb99L, 0xffc304a5L,
    0x4d351805L, 0x7f3d5ce3L, 0xa6c866c6L, 0x5d5bcca9L,
    0xdaec6feaL, 0x9f926f91L, 0x9f46222fL, 0x3991467dL,
    0xa5bf6d8eL, 0x1143c44fL, 0x43958302L, 0xd0214eebL,
    0x022083b8L, 0x3fb6180cL, 0x18f8931eL, 0x281658e6L,
    0x26486e3eL, 0x8bd78a70L, 0x7477e4c1L, 0xb506e07cL,
    0xf32d0a25L, 0x79098b02L, 0xe4eabb81L, 0x28123b23L,
    0x69dead38L, 0x1574ca16L, 0xdf871b62L, 0x211c40b7L,
    0xa51a9ef9L, 0x0014377bL, 0x041e8ac8L, 0x09114003L,
    0xbd59e4d2L, 0xe3d156d5L, 0x4fe876d5L, 0x2f91a340L,
    0x557be8deL, 0x00eae4a7L, 0x0ce5c2ecL, 0x4db4bba6L,
    0xe756bdffL, 0xdd3369acL, 0xec17b035L, 0x06572327L,
    0x99afc8b0L, 0x56c8c391L, 0x6b65811cL, 0x5e146119L,
    0x6e85cb75L, 0xbe07c002L, 0xc2325577L, 0x893ff4ecL,
    0x5bbfc92dL, 0xd0ec3b25L, 0xb7801ab7L, 0x8d6d3b24L,
    0x20c763efL, 0xc366a5fcL, 0x9c382880L, 0x0ace3205L,
    0xaac9548aL, 0xeca1d7c7L, 0x041afa32L, 0x1d16625aL,
    0x6701902cL, 0x9b757a54L, 0x31d477f7L, 0x9126b031L,
    0x36cc6fdbL, 0xc70b8b46L, 0xd9e66a48L, 0x56e55a79L,
    0x026a4cebL, 0x52437effL, 0x2f8f76b4L, 0x0df980a5L,
    0x8674cde3L, 0xedda04ebL, 0x17a9be04L, 0x2c18f4dfL,
    0xb7747f9dL, 0xab2af7b4L, 0xefc34d20L, 0x2e096b7cL,
    0x1741a254L, 0xe5b6a035L, 0x213d42f6L, 0x2c1c7c26L,
    0x61c2f50fL, 0x6552daf9L, 0xd2c231f8L, 0x25130f69L,
    0xd8167fa2L, 0x0418f2c8L, 0x001a96a6L, 0x0d1526abL,
    0x63315c21L, 0x5e0a72ecL, 0x49bafefdL, 0x187908d9L,
    0x8d0dbd86L, 0x311170a7L, 0x3e9b640cL, 0xcc3e10d7L,
    0xd5cad3b6L, 0x0caec388L, 0xf73001e1L, 0x6c728affL,
    0x71eae2a1L, 0x1f9af36eL, 0xcfcbd12fL, 0xc1de8417L,
    0xac07be6bL, 0xcb44a1d8L, 0x8b9b0f56L, 0x013988c3L,
    0xb1c52fcaL, 0xb4be31cdL, 0xd8782806L, 0x12a3a4e2L,
    0x6f7de532L, 0x58fd7eb6L, 0xd01ee900L, 0x24adffc2L,
    0xf4990fc5L, 0x9711aac5L, 0x001d7b95L, 0x82e5e7d2L,
    0x109873f6L, 0x00613096L, 0xc32d9521L, 0xada121ffL,
    0x29908415L, 0x7fbb977fL, 0xaf9eb3dbL, 0x29c9ed2aL,
    0x5ce2a465L, 0xa730f32cL, 0xd0aa3fe8L, 0x8a5cc091L,
    0xd49e2ce7L, 0x0ce454a9L, 0xd60acd86L, 0x015f1919L,
    0x77079103L, 0xdea03af6L, 0x78a8565eL, 0xdee356dfL,
    0x21f05cbeL, 0x8b75e387L, 0xb3c50651L, 0xb8a5c3efL,
    0xd8eeb6d2L, 0xe523be77L, 0xc2154529L, 0x2f69efdfL,
    0xafe67afbL, 0xf470c4b2L, 0xf3e0eb5bL, 0xd6cc9876L,
    0x39e4460cL, 0x1fda8538L, 0x1987832fL, 0xca007367L,
    0xa99144f8L, 0x296b299eL, 0x492fc295L, 0x9266beabL,
    0xb5676e69L, 0x9bd3dddaL, 0xdf7e052fL, 0xdb25701cL,
    0x1b5e51eeL, 0xf65324e6L, 0x6afce36cL, 0x0316cc04L,
    0x8644213eL, 0xb7dc59d0L, 0x7965291fL, 0xccd6fd43L,
    0x41823979L, 0x932bcdf6L, 0xb657c34dL, 0x4edfd282L,
    0x7ae5290cL, 0x3cb9536bL, 0x851e20feL, 0x9833557eL,
    0x13ecf0b0L, 0xd3ffb372L, 0x3f85c5c1L, 0x0aef7ed2L,
};

static uint32_t S5[SBOXLEN] = {
    0x7ec90c04L, 0x2c6e74b9L, 0x9b0e66dfL, 0xa6337911L,
    0xb86a7fffL, 0x1dd358f5L, 0x44dd9d44L, 0x1731167fL,
    0x08fbf1faL, 0xe7f511ccL, 0xd2051b00L, 0x735aba00L,
    0x2ab722d8L, 0x386381cbL, 0xacf6243aL, 0x69befd7aL,
    0xe6a2e77fL, 0xf0c720cdL, 0xc4494816L, 0xccf5c180L,
    0x38851640L, 0x15b0a848L, 0xe68b18cbL, 0x4caadeffL,
    0x5f480a01L, 0x0412b2aaL, 0x259814fcL, 0x41d0efe2L,
    0x4e40b48dL, 0x248eb6fbL, 0x8dba1cfeL, 0x41a99b02L,
    0x1a550a04L, 0xba8f65cbL, 0x7251f4e7L, 0x95a51725L,
    0xc106ecd7L, 0x97a5980aL, 0xc539b9aaL, 0x4d79fe6aL,
    0xf2f3f763L, 0x68af8040L, 0xed0c9e56L, 0x11b4958bL,
    0xe1eb5a88L, 0x8709e6b0L, 0xd7e07156L, 0x4e29fea7L,
    0x6366e52dL, 0x02d1c000L, 0xc4ac8e05L, 0x9377f571L,
    0x0c05372aL, 0x578535f2L, 0x2261be02L, 0xd642a0c9L,
    0xdf13a280L, 0x74b55bd2L, 0x682199c0L, 0xd421e5ecL,
    0x53fb3ce8L, 0xc8adedb3L, 0x28a87fc9L, 0x3d959981L,
    0x5c1ff900L, 0xfe38d399L, 0x0c4eff0bL, 0x062407eaL,
    0xaa2f4fb1L, 0x4fb96976L, 0x90c79505L, 0xb0a8a774L,
    0xef55a1ffL, 0xe59ca2c2L, 0xa6b62d27L, 0xe66a4263L,
    0xdf65001fL, 0x0ec50966L, 0xdfdd55bcL, 0x29de0655L,
    0x911e739aL, 0x17af8975L, 0x32c7911cL, 0x89f89468L,
    0x0d01e980L, 0x524755f4L, 0x03b63cc9L, 0x0cc844b2L,
    0xbcf3f0aaL, 0x87ac36e9L, 0xe53a7426L, 0x01b3d82bL,
    0x1a9e7449L, 0x64ee2d7eL, 0xcddbb1daL, 0x01c94910L,
    0xb868bf80L, 0x0d26f3fdL, 0x9342ede7L, 0x04a5c284L,
    0x636737b6L, 0x50f5b616L, 0xf24766e3L, 0x8eca36c1L,
    0x136e05dbL, 0xfef18391L, 0xfb887a37L, 0xd6e7f7d4L,
    0xc7fb7dc9L, 0x3063fcdfL, 0xb6f589deL, 0xec2941daL,
    0x26e46695L, 0xb7566419L, 0xf654efc5L, 0xd08d58b7L,
    0x48925401L, 0xc1bacb7fL, 0xe5ff550fL, 0xb6083049L,
    0x5bb5d0e8L, 0x87d72e5aL, 0xab6a6ee1L, 0x223a66ceL,
    0xc62bf3cdL, 0x9e0885f9L, 0x68cb3e47L, 0x086c010fL,
    0xa21de820L, 0xd18b69deL, 0xf3f65777L, 0xfa02c3f6L,
    0x407edac3L, 0xcbb3d550L, 0x1793084dL, 0xb0d70ebaL,
    0x0ab378d5L, 0xd951fb0cL, 0xded7da56L, 0x4124bbe4L,
    0x94ca0b56L, 0x0f5755d1L, 0xe0e1e56eL, 0x6184b5beL,
    0x580a249fL, 0x94f74bc0L, 0xe327888eL, 0x9f7b5561L,
    0xc3dc0280L, 0x05687715L, 0x646c6bd7L, 0x44904db3L,
    0x66b4f0a3L, 0xc0f1648aL, 0x697ed5afL, 0x49e92ff6L,
    0x309e374fL, 0x2cb6356aL, 0x85808573L, 0x4991f840L,
    0x76f0ae02L, 0x083be84dL, 0x28421c9aL, 0x44489406L,
    0x736e4cb8L, 0xc1092910L, 0x8bc95fc6L, 0x7d869cf4L,
    0x134f616fL, 0x2e77118dL, 0xb31b2be1L, 0xaa90b472L,
    0x3ca5d717L, 0x7d161bbaL, 0x9cad9010L, 0xaf462ba2L,
    0x9fe459d2L, 0x45d34559L, 0xd9f2da13L, 0xdbc65487L,
    0xf3e4f94eL, 0x176d486fL, 0x097c13eaL, 0x631da5c7L,
    0x445f7382L, 0x175683f4L, 0xcdc66a97L, 0x70be0288L,
    0xb3cdcf72L, 0x6e5dd2f3L, 0x20936079L, 0x459b80a5L,
    0xbe60e2dbL, 0xa9c23101L, 0xeba5315cL, 0x224e42f2L,
    0x1c5c1572L, 0xf6721b2cL, 0x1ad2fff3L, 0x8c25404eL,
    0x324ed72fL, 0x4067b7fdL, 0x0523138eL, 0x5ca3bc78L,
    0xdc0fd66eL, 0x75922283L, 0x784d6b17L, 0x58ebb16eL,
    0x44094f85L, 0x3f481d87L, 0xfcfeae7bL, 0x77b5ff76L,
    0x8c2302bfL, 0xaaf47556L, 0x5f46b02aL, 0x2b092801L,
    0x3d38f5f7L, 0x0ca81f36L, 0x52af4a8aL, 0x66d5e7c0L,
    0xdf3b0874L, 0x95055110L, 0x1b5ad7a8L, 0xf61ed5adL,
    0x6cf6e479L, 0x20758184L, 0xd0cefa65L, 0x88f7be58L,
    0x4a046826L, 0x0ff6f8f3L, 0xa09c7f70L, 0x5346aba0L,
    0x5ce96c28L, 0xe176eda3L, 0x6bac307fL, 0x376829d2L,
    0x85360fa9L, 0x17e3fe2aL, 0x24b79767L, 0xf5a96b20L,
    0xd6cd2595L, 0x68ff1ebfL, 0x7555442cL, 0xf19f06beL,
    0xf9e0659aL, 0xeeb9491dL, 0x34010718L, 0xbb30cab8L,
    0xe822fe15L, 0x88570983L, 0x750e6249L, 0xda627e55L,
    0x5e76ffa8L, 0xb1534546L, 0x6d47de08L, 0xefe9e7d4L,
};

static uint32_t S6[SBOXLEN] = {
    0xf6fa8f9dL, 0x2cac6ce1L, 0x4ca34867L, 0xe2337f7cL,
    0x95db08e7L, 0x016843b4L, 0xeced5cbcL, 0x325553acL,
    0xbf9f0960L, 0xdfa1e2edL, 0x83f0579dL, 0x63ed86b9L,
    0x1ab6a6b8L, 0xde5ebe39L, 0xf38ff732L, 0x8989b138L,
    0x33f14961L, 0xc01937bdL, 0xf506c6daL, 0xe4625e7eL,
    0xa308ea99L, 0x4e23e33cL, 0x79cbd7ccL, 0x48a14367L,
    0xa3149619L, 0xfec94bd5L, 0xa114174aL, 0xeaa01866L,
    0xa084db2dL, 0x09a8486fL, 0xa888614aL, 0x2900af98L,
    0x01665991L, 0xe1992863L, 0xc8f30c60L, 0x2e78ef3cL,
    0xd0d51932L, 0xcf0fec14L, 0xf7ca07d2L, 0xd0a82072L,
    0xfd41197eL, 0x9305a6b0L, 0xe86be3daL, 0x74bed3cdL,
    0x372da53cL, 0x4c7f4448L, 0xdab5d440L, 0x6dba0ec3L,
    0x083919a7L, 0x9fbaeed9L, 0x49dbcfb0L, 0x4e670c53L,
    0x5c3d9c01L, 0x64bdb941L, 0x2c0e636aL, 0xba7dd9cdL,
    0xea6f7388L, 0xe70bc762L, 0x35f29adbL, 0x5c4cdd8dL,
    0xf0d48d8cL, 0xb88153e2L, 0x08a19866L, 0x1ae2eac8L,
    0x284caf89L, 0xaa928223L, 0x9334be53L, 0x3b3a21bfL,
    0x16434be3L, 0x9aea3906L, 0xefe8c36eL, 0xf890cdd9L,
    0x80226daeL, 0xc340a4a3L, 0xdf7e9c09L, 0xa694a807L,
    0x5b7c5eccL, 0x221db3a6L, 0x9a69a02fL, 0x68818a54L,
    0xceb2296fL, 0x53c0843aL, 0xfe893655L, 0x25bfe68aL,
    0xb4628abcL, 0xcf222ebfL, 0x25ac6f48L, 0xa9a99387L,
    0x53bddb65L, 0xe76ffbe7L, 0xe967fd78L, 0x0ba93563L,
    0x8e342bc1L, 0xe8a11be9L, 0x4980740dL, 0xc8087dfcL,
    0x8de4bf99L, 0xa11101a0L, 0x7fd37975L, 0xda5a26c0L,
    0xe81f994fL, 0x9528cd89L, 0xfd339fedL, 0xb87834bfL,
    0x5f04456dL, 0x22258698L, 0xc9c4c83bL, 0x2dc156beL,
    0x4f628daaL, 0x57f55ec5L, 0xe2220abeL, 0xd2916ebfL,
    0x4ec75b95L, 0x24f2c3c0L, 0x42d15d99L, 0xcd0d7fa0L,
    0x7b6e27ffL, 0xa8dc8af0L, 0x7345c106L, 0xf41e232fL,
    0x35162386L, 0xe6ea8926L, 0x3333b094L, 0x157ec6f2L,
    0x372b74afL, 0x692573e4L, 0xe9a9d848L, 0xf3160289L,
    0x3a62ef1dL, 0xa787e238L, 0xf3a5f676L, 0x74364853L,
    0x20951063L, 0x4576698dL, 0xb6fad407L, 0x592af950L,
    0x36f73523L, 0x4cfb6e87L, 0x7da4cec0L, 0x6c152daaL,
    0xcb0396a8L, 0xc50dfe5dL, 0xfcd707abL, 0x0921c42fL,
    0x89dff0bbL, 0x5fe2be78L, 0x448f4f33L, 0x754613c9L,
    0x2b05d08dL, 0x48b9d585L, 0xdc049441L, 0xc8098f9bL,
    0x7dede786L, 0xc39a3373L, 0x42410005L, 0x6a091751L,
    0x0ef3c8a6L, 0x890072d6L, 0x28207682L, 0xa9a9f7beL,
    0xbf32679dL, 0xd45b5b75L, 0xb353fd00L, 0xcbb0e358L,
    0x830f220aL, 0x1f8fb214L, 0xd372cf08L, 0xcc3c4a13L,
    0x8cf63166L, 0x061c87beL, 0x88c98f88L, 0x6062e397L,
    0x47cf8e7aL, 0xb6c85283L, 0x3cc2acfbL, 0x3fc06976L,
    0x4e8f0252L, 0x64d8314dL, 0xda3870e3L, 0x1e665459L,
    0xc10908f0L, 0x513021a5L, 0x6c5b68b7L, 0x822f8aa0L,
    0x3007cd3eL, 0x74719eefL, 0xdc872681L, 0x073340d4L,
    0x7e432fd9L, 0x0c5ec241L, 0x8809286cL, 0xf592d891L,
    0x08a930f6L, 0x957ef305L, 0xb7fbffbdL, 0xc266e96fL,
    0x6fe4ac98L, 0xb173ecc0L, 0xbc60b42aL, 0x953498daL,
    0xfba1ae12L, 0x2d4bd736L, 0x0f25faabL, 0xa4f3fcebL,
    0xe2969123L, 0x257f0c3dL, 0x9348af49L, 0x361400bcL,
    0xe8816f4aL, 0x3814f200L, 0xa3f94043L, 0x9c7a54c2L,
    0xbc704f57L, 0xda41e7f9L, 0xc25ad33aL, 0x54f4a084L,
    0xb17f5505L, 0x59357cbeL, 0xedbd15c8L, 0x7f97c5abL,
    0xba5ac7b5L, 0xb6f6deafL, 0x3a479c3aL, 0x5302da25L,
    0x653d7e6aL, 0x54268d49L, 0x51a477eaL, 0x5017d55bL,
    0xd7d25d88L, 0x44136c76L, 0x0404a8c8L, 0xb8e5a121L,
    0xb81a928aL, 0x60ed5869L, 0x97c55b96L, 0xeaec991bL,
    0x29935913L, 0x01fdb7f1L, 0x088e8dfaL, 0x9ab6f6f5L,
    0x3b4cbf9fL, 0x4a5de3abL, 0xe6051d35L, 0xa0e1d855L,
    0xd36b4cf1L, 0xf544edebL, 0xb0e93524L, 0xbebb8fbdL,
    0xa2d762cfL, 0x49c92f54L, 0x38b5f331L, 0x7128a454L,
    0x48392905L, 0xa65b1db8L, 0x851c97bdL, 0xd675cf2fL,
};

static uint32_t S7[SBOXLEN] = {
    0x85e04019L, 0x332bf567L, 0x662dbfffL, 0xcfc65693L,
    0x2a8d7f6fL, 0xab9bc912L, 0xde6008a1L, 0x2028da1fL,
    0x0227bce7L, 0x4d642916L, 0x18fac300L, 0x50f18b82L,
    0x2cb2cb11L, 0xb232e75cL, 0x4b3695f2L, 0xb28707deL,
    0xa05fbcf6L, 0xcd4181e9L, 0xe150210cL, 0xe24ef1bdL,
    0xb168c381L, 0xfde4e789L, 0x5c79b0d8L, 0x1e8bfd43L,
    0x4d495001L, 0x38be4341L, 0x913cee1dL, 0x92a79c3fL,
    0x089766beL, 0xbaeeadf4L, 0x1286becfL, 0xb6eacb19L,
    0x2660c200L, 0x7565bde4L, 0x64241f7aL, 0x8248dca9L,
    0xc3b3ad66L, 0x28136086L, 0x0bd8dfa8L, 0x356d1cf2L,
    0x107789beL, 0xb3b2e9ceL, 0x0502aa8fL, 0x0bc0351eL,
    0x166bf52aL, 0xeb12ff82L, 0xe3486911L, 0xd34d7516L,
    0x4e7b3affL, 0x5f43671bL, 0x9cf6e037L, 0x4981ac83L,
    0x334266ceL, 0x8c9341b7L, 0xd0d854c0L, 0xcb3a6c88L,
    0x47bc2829L, 0x4725ba37L, 0xa66ad22bL, 0x7ad61f1eL,
    0x0c5cbafaL, 0x4437f107L, 0xb6e79962L, 0x42d2d816L,
    0x0a961288L, 0xe1a5c06eL, 0x13749e67L, 0x72fc081aL,
    0xb1d139f7L, 0xf9583745L, 0xcf19df58L, 0xbec3f756L,
    0xc06eba30L, 0x07211b24L, 0x45c28829L, 0xc95e317fL,
    0xbc8ec511L, 0x38bc46e9L, 0xc6e6fa14L, 0xbae8584aL,
    0xad4ebc46L, 0x468f508bL, 0x7829435fL, 0xf124183bL,
    0x821dba9fL, 0xaff60ff4L, 0xea2c4e6dL, 0x16e39264L,
    0x92544a8bL, 0x009b4fc3L, 0xaba68cedL, 0x9ac96f78L,
    0x06a5b79aL, 0xb2856e6eL, 0x1aec3ca9L, 0xbe838688L,
    0x0e0804e9L, 0x55f1be56L, 0xe7e5363bL, 0xb3a1f25dL,
    0xf7debb85L, 0x61fe033cL, 0x16746233L, 0x3c034c28L,
    0xda6d0c74L, 0x79aac56cL, 0x3ce4e1adL, 0x51f0c802L,
    0x98f8f35aL, 0x1626a49fL, 0xeed82b29L, 0x1d382fe3L,
    0x0c4fb99aL, 0xbb325778L, 0x3ec6d97bL, 0x6e77a6a9L,
    0xcb658b5cL, 0xd45230c7L, 0x2bd1408bL, 0x60c03eb7L,
    0xb9068d78L, 0xa33754f4L, 0xf430c87dL, 0xc8a71302L,
    0xb96d8c32L, 0xebd4e7beL, 0xbe8b9d2dL, 0x7979fb06L,
    0xe7225308L, 0x8b75cf77L, 0x11ef8da4L, 0xe083c858L,
    0x8d6b786fL, 0x5a6317a6L, 0xfa5cf7a0L, 0x5dda0033L,
    0xf28ebfb0L, 0xf5b9c310L, 0xa0eac280L, 0x08b9767aL,
    0xa3d9d2b0L, 0x79d34217L, 0x021a718dL, 0x9ac6336aL,
    0x2711fd60L, 0x438050e3L, 0x069908a8L, 0x3d7fedc4L,
    0x826d2befL, 0x4eeb8476L, 0x488dcf25L, 0x36c9d566L,
    0x28e74e41L, 0xc2610acaL, 0x3d49a9cfL, 0xbae3b9dfL,
    0xb65f8de6L, 0x92aeaf64L, 0x3ac7d5e6L, 0x9ea80509L,
    0xf22b017dL, 0xa4173f70L, 0xdd1e16c3L, 0x15e0d7f9L,
    0x50b1b887L, 0x2b9f4fd5L, 0x625aba82L, 0x6a017962L,
    0x2ec01b9cL, 0x15488aa9L, 0xd716e740L, 0x40055a2cL,
    0x93d29a22L, 0xe32dbf9aL, 0x058745b9L, 0x3453dc1eL,
    0xd699296eL, 0x496cff6fL, 0x1c9f4986L, 0xdfe2ed07L,
    0xb87242d1L, 0x19de7eaeL, 0x053e561aL, 0x15ad6f8cL,
    0x66626c1cL, 0x7154c24cL, 0xea082b2aL, 0x93eb2939L,
    0x17dcb0f0L, 0x58d4f2aeL, 0x9ea294fbL, 0x52cf564cL,
    0x9883fe66L, 0x2ec40581L, 0x763953c3L, 0x01d6692eL,
    0xd3a0c108L, 0xa1e7160eL, 0xe4f2dfa6L, 0x693ed285L,
    0x74904698L, 0x4c2b0eddL, 0x4f757656L, 0x5d393378L,
    0xa132234fL, 0x3d321c5dL, 0xc3f5e194L, 0x4b269301L,
    0xc79f022fL, 0x3c997e7eL, 0x5e4f9504L, 0x3ffafbbdL,
    0x76f7ad0eL, 0x296693f4L, 0x3d1fce6fL, 0xc61e45beL,
    0xd3b5ab34L, 0xf72bf9b7L, 0x1b0434c0L, 0x4e72b567L,
    0x5592a33dL, 0xb5229301L, 0xcfd2a87fL, 0x60aeb767L,
    0x1814386bL, 0x30bcc33dL, 0x38a0c07dL, 0xfd1606f2L,
    0xc363519bL, 0x589dd390L, 0x5479f8e6L, 0x1cb8d647L,
    0x97fd61a9L, 0xea7759f4L, 0x2d57539dL, 0x569a58cfL,
    0xe84e63adL, 0x462e1b78L, 0x6580f87eL, 0xf3817914L,
    0x91da55f4L, 0x40a230f3L, 0xd1988f35L, 0xb6e318d2L,
    0x3ffa50bcL, 0x3d40f021L, 0xc3c0bdaeL, 0x4958c24cL,
    0x518f36b2L, 0x84b1d370L, 0x0fedce83L, 0x878ddadaL,
    0xf2a279c7L, 0x94e01be8L, 0x90716f4bL, 0x954b8aa3L,
};

static uint32_t S8[SBOXLEN] = {
    0xe216300dL, 0xbbddfffcL, 0xa7ebdabdL, 0x35648095L,
    0x7789f8b7L, 0xe6c1121bL, 0x0e241600L, 0x052ce8b5L,
    0x11a9cfb0L, 0xe5952f11L, 0xece7990aL, 0x9386d174L,
    0x2a42931cL, 0x76e38111L, 0xb12def3aL, 0x37ddddfcL,
    0xde9adeb1L, 0x0a0cc32cL, 0xbe197029L, 0x84a00940L,
    0xbb243a0fL, 0xb4d137cfL, 0xb44e79f0L, 0x049eedfdL,
    0x0b15a15dL, 0x480d3168L, 0x8bbbde5aL, 0x669ded42L,
    0xc7ece831L, 0x3f8f95e7L, 0x72df191bL, 0x7580330dL,
    0x94074251L, 0x5c7dcdfaL, 0xabbe6d63L, 0xaa402164L,
    0xb301d40aL, 0x02e7d1caL, 0x53571daeL, 0x7a3182a2L,
    0x12a8ddecL, 0xfdaa335dL, 0x176f43e8L, 0x71fb46d4L,
    0x38129022L, 0xce949ad4L, 0xb84769adL, 0x965bd862L,
    0x82f3d055L, 0x66fb9767L, 0x15b80b4eL, 0x1d5b47a0L,
    0x4cfde06fL, 0xc28ec4b8L, 0x57e8726eL, 0x647a78fcL,
    0x99865d44L, 0x608bd593L, 0x6c200e03L, 0x39dc5ff6L,
    0x5d0b00a3L, 0xae63aff2L, 0x7e8bd632L, 0x70108c0cL,
    0xbbd35049L, 0x2998df04L, 0x980cf42aL, 0x9b6df491L,
    0x9e7edd53L, 0x06918548L, 0x58cb7e07L, 0x3b74ef2eL,
    0x522fffb1L, 0xd24708ccL, 0x1c7e27cdL, 0xa4eb215bL,
    0x3cf1d2e2L, 0x19b47a38L, 0x424f7618L, 0x35856039L,
    0x9d17dee7L, 0x27eb35e6L, 0xc9aff67bL, 0x36baf5b8L,
    0x09c467cdL, 0xc18910b1L, 0xe11dbf7bL, 0x06cd1af8L,
    0x7170c608L, 0x2d5e3354L, 0xd4de495aL, 0x64c6d006L,
    0xbcc0c62cL, 0x3dd00db3L, 0x708f8f34L, 0x77d51b42L,
    0x264f620fL, 0x24b8d2bfL, 0x15c1b79eL, 0x46a52564L,
    0xf8d7e54eL, 0x3e378160L, 0x7895cda5L, 0x859c15a5L,
    0xe6459788L, 0xc37bc75fL, 0xdb07ba0cL, 0x0676a3abL,
    0x7f229b1eL, 0x31842e7bL, 0x24259fd7L, 0xf8bef472L,
    0x835ffcb8L, 0x6df4c1f2L, 0x96f5b195L, 0xfd0af0fcL,
    0xb0fe134cL, 0xe2506d3dL, 0x4f9b12eaL, 0xf215f225L,
    0xa223736fL, 0x9fb4c428L, 0x25d04979L, 0x34c713f8L,
    0xc4618187L, 0xea7a6e98L, 0x7cd16efcL, 0x1436876cL,
    0xf1544107L, 0xbedeee14L, 0x56e9af27L, 0xa04aa441L,
    0x3cf7c899L, 0x92ecbae6L, 0xdd67016dL, 0x151682ebL,
    0xa842eedfL, 0xfdba60b4L, 0xf1907b75L, 0x20e3030fL,
    0x24d8c29eL, 0xe139673bL, 0xefa63fb8L, 0x71873054L,
    0xb6f2cf3bL, 0x9f326442L, 0xcb15a4ccL, 0xb01a4504L,
    0xf1e47d8dL, 0x844a1be5L, 0xbae7dfdcL, 0x42cbda70L,
    0xcd7dae0aL, 0x57e85b7aL, 0xd53f5af6L, 0x20cf4d8cL,
    0xcea4d428L, 0x79d130a4L, 0x3486ebfbL, 0x33d3cddcL,
    0x77853b53L, 0x37effcb5L, 0xc5068778L, 0xe580b3e6L,
    0x4e68b8f4L, 0xc5c8b37eL, 0x0d809ea2L, 0x398feb7cL,
    0x132a4f94L, 0x43b7950eL, 0x2fee7d1cL, 0x223613bdL,
    0xdd06caa2L, 0x37df932bL, 0xc4248289L, 0xacf3ebc3L,
    0x5715f6b7L, 0xef3478ddL, 0xf267616fL, 0xc148cbe4L,
    0x9052815eL, 0x5e410fabL, 0xb48a2465L, 0x2eda7fa4L,
    0xe87b40e4L, 0xe98ea084L, 0x5889e9e1L, 0xefd390fcL,
    0xdd07d35bL, 0xdb485694L, 0x38d7e5b2L, 0x57720101L,
    0x730edebcL, 0x5b643113L, 0x94917e4fL, 0x503c2fbaL,
    0x646f1282L, 0x7523d24aL, 0xe0779695L, 0xf9c17a8fL,
    0x7a5b2121L, 0xd187b896L, 0x29263a4dL, 0xba510cdfL,
    0x81f47c9fL, 0xad1163edL, 0xea7b5965L, 0x1a00726eL,
    0x11403092L, 0x00da6d77L, 0x4a0cdd61L, 0xad1f4603L,
    0x605bdfb0L, 0x9eedc364L, 0x22ebe6a8L, 0xcee7d28aL,
    0xa0e736a0L, 0x5564a6b9L, 0x10853209L, 0xc7eb8f37L,
    0x2de705caL, 0x8951570fL, 0xdf09822bL, 0xbd691a6cL,
    0xaa12e4f2L, 0x87451c0fL, 0xe0f6a27aL, 0x3ada4819L,
    0x4cf1764fL, 0x0d771c2bL, 0x67cdb156L, 0x350d8384L,
    0x5938fa0fL, 0x42399ef3L, 0x36997b07L, 0x0e84093dL,
    0x4aa93e61L, 0x8360d87bL, 0x1fa98b0cL, 0x1149382cL,
    0xe97625a5L, 0x0614d1b7L, 0x0e25244bL, 0x0c768347L,
    0x589e8d82L, 0x0d2059d1L, 0xa466bb1eL, 0xf8da0a82L,
    0x04f19130L, 0xba6e4ec0L, 0x99265164L, 0x1ee7230dL,
    0x50b2ad80L, 0xeaee6801L, 0x8db2a283L, 0xea8bf59eL,
};

typedef struct {
    uint32_t K[SUBKEYS];
    boolean rounds16;
}
CAST5, *pCAST5;

#define BYTE0(s) (uint8_t)((s>>24)&0xFF)
#define BYTE1(s) (uint8_t)((s>>16)&0xFF)
#define BYTE2(s) (uint8_t)((s>> 8)&0xFF)
#define BYTE3(s) (uint8_t)((s    )&0xFF)

/* extract low 5 bytes */
#define MASK(s)  ((s) & 0x1F);

#define x0 x[0]
#define x1 x[1]
#define x2 x[2]
#define x3 x[3]
#define x4 x[4]
#define x5 x[5]
#define x6 x[6]
#define x7 x[7]
#define x8 x[8]
#define x9 x[9]
#define xA x[10]
#define xB x[11]
#define xC x[12]
#define xD x[13]
#define xE x[14]
#define xF x[15]

#define z0 z[0]
#define z1 z[1]
#define z2 z[2]
#define z3 z[3]
#define z4 z[4]
#define z5 z[5]
#define z6 z[6]
#define z7 z[7]
#define z8 z[8]
#define z9 z[9]
#define zA z[10]
#define zB z[11]
#define zC z[12]
#define zD z[13]
#define zE z[14]
#define zF z[15]


/* Key is MSB first */
#ifdef __BORLANDC__
#pragma warn -aus
#endif
static void generateKeySchedule(uint8_t x[KEYSIZE], pCAST5 ks)
{
    uint8_t z[KEYSIZE];
    uint32_t temp;

    temp = S5[xD] ^ S6[xF] ^ S7[xC] ^ S8[xE] ^ S7[x8];
    z0 = x0 ^ BYTE0(temp);
    z1 = x1 ^ BYTE1(temp);
    z2 = x2 ^ BYTE2(temp);
    z3 = x3 ^ BYTE3(temp);

    temp = S5[z0] ^ S6[z2] ^ S7[z1] ^ S8[z3] ^ S8[xA];
    z4 = x8 ^ BYTE0(temp);
    z5 = x9 ^ BYTE1(temp);
    z6 = xA ^ BYTE2(temp);
    z7 = xB ^ BYTE3(temp);

    temp = S5[z7] ^ S6[z6] ^ S7[z5] ^ S8[z4] ^ S5[x9];
    z8 = xC ^ BYTE0(temp);
    z9 = xD ^ BYTE1(temp);
    zA = xE ^ BYTE2(temp);
    zB = xF ^ BYTE3(temp);

    temp = S5[zA] ^ S6[z9] ^ S7[zB] ^ S8[z8] ^ S6[xB];
    zC = x4 ^ BYTE0(temp);
    zD = x5 ^ BYTE1(temp);
    zE = x6 ^ BYTE2(temp);
    zF = x7 ^ BYTE3(temp);

    ks->K[0] = S5[z8] ^ S6[z9] ^ S7[z7] ^ S8[z6] ^ S5[z2];
    ks->K[1] = S5[zA] ^ S6[zB] ^ S7[z5] ^ S8[z4] ^ S6[z6];
    ks->K[2] = S5[zC] ^ S6[zD] ^ S7[z3] ^ S8[z2] ^ S7[z9];
    ks->K[3] = S5[zE] ^ S6[zF] ^ S7[z1] ^ S8[z0] ^ S8[zC];

    temp = S5[z5] ^ S6[z7] ^ S7[z4] ^ S8[z6] ^ S7[z0];
    x0 = z8 ^ BYTE0(temp);
    x1 = z9 ^ BYTE1(temp);
    x2 = zA ^ BYTE2(temp);
    x3 = zB ^ BYTE3(temp);

    temp = S5[x0] ^ S6[x2] ^ S7[x1] ^ S8[x3] ^ S8[z2];
    x4 = z0 ^ BYTE0(temp);
    x5 = z1 ^ BYTE1(temp);
    x6 = z2 ^ BYTE2(temp);
    x7 = z3 ^ BYTE3(temp);

    temp = S5[x7] ^ S6[x6] ^ S7[x5] ^ S8[x4] ^ S5[z1];
    x8 = z4 ^ BYTE0(temp);
    x9 = z5 ^ BYTE1(temp);
    xA = z6 ^ BYTE2(temp);
    xB = z7 ^ BYTE3(temp);

    temp = S5[xA] ^ S6[x9] ^ S7[xB] ^ S8[x8] ^ S6[z3];
    xC = zC ^ BYTE0(temp);
    xD = zD ^ BYTE1(temp);
    xE = zE ^ BYTE2(temp);
    xF = zF ^ BYTE3(temp);

    ks->K[4] = S5[x3] ^ S6[x2] ^ S7[xC] ^ S8[xD] ^ S5[x8];
    ks->K[5] = S5[x1] ^ S6[x0] ^ S7[xE] ^ S8[xF] ^ S6[xD];
    ks->K[6] = S5[x7] ^ S6[x6] ^ S7[x8] ^ S8[x9] ^ S7[x3];
    ks->K[7] = S5[x5] ^ S6[x4] ^ S7[xA] ^ S8[xB] ^ S8[x7];

    temp = S5[xD] ^ S6[xF] ^ S7[xC] ^ S8[xE] ^ S7[x8];
    z0 = x0 ^ BYTE0(temp);
    z1 = x1 ^ BYTE1(temp);
    z2 = x2 ^ BYTE2(temp);
    z3 = x3 ^ BYTE3(temp);

    temp = S5[z0] ^ S6[z2] ^ S7[z1] ^ S8[z3] ^ S8[xA];
    z4 = x8 ^ BYTE0(temp);
    z5 = x9 ^ BYTE1(temp);
    z6 = xA ^ BYTE2(temp);
    z7 = xB ^ BYTE3(temp);

    temp = S5[z7] ^ S6[z6] ^ S7[z5] ^ S8[z4] ^ S5[x9];
    z8 = xC ^ BYTE0(temp);
    z9 = xD ^ BYTE1(temp);
    zA = xE ^ BYTE2(temp);
    zB = xF ^ BYTE3(temp);

    temp = S5[zA] ^ S6[z9] ^ S7[zB] ^ S8[z8] ^ S6[xB];
    zC = x4 ^ BYTE0(temp);
    zD = x5 ^ BYTE1(temp);
    zE = x6 ^ BYTE2(temp);
    zF = x7 ^ BYTE3(temp);

    ks->K[8] = S5[z3] ^ S6[z2] ^ S7[zC] ^ S8[zD] ^ S5[z9];
    ks->K[9] = S5[z1] ^ S6[z0] ^ S7[zE] ^ S8[zF] ^ S6[zC];
    ks->K[10] = S5[z7] ^ S6[z6] ^ S7[z8] ^ S8[z9] ^ S7[z2];
    ks->K[11] = S5[z5] ^ S6[z4] ^ S7[zA] ^ S8[zB] ^ S8[z6];


    temp = S5[z5] ^ S6[z7] ^ S7[z4] ^ S8[z6] ^ S7[z0];
    x0 = z8 ^ BYTE0(temp);
    x1 = z9 ^ BYTE1(temp);
    x2 = zA ^ BYTE2(temp);
    x3 = zB ^ BYTE3(temp);

    temp = S5[x0] ^ S6[x2] ^ S7[x1] ^ S8[x3] ^ S8[z2];
    x4 = z0 ^ BYTE0(temp);
    x5 = z1 ^ BYTE1(temp);
    x6 = z2 ^ BYTE2(temp);
    x7 = z3 ^ BYTE3(temp);

    temp = S5[x7] ^ S6[x6] ^ S7[x5] ^ S8[x4] ^ S5[z1];
    x8 = z4 ^ BYTE0(temp);
    x9 = z5 ^ BYTE1(temp);
    xA = z6 ^ BYTE2(temp);
    xB = z7 ^ BYTE3(temp);

    temp = S5[xA] ^ S6[x9] ^ S7[xB] ^ S8[x8] ^ S6[z3];
    xC = zC ^ BYTE0(temp);
    xD = zD ^ BYTE1(temp);
    xE = zE ^ BYTE2(temp);
    xF = zF ^ BYTE3(temp);

    ks->K[12] = S5[x8] ^ S6[x9] ^ S7[x7] ^ S8[x6] ^ S5[x3];
    ks->K[13] = S5[xA] ^ S6[xB] ^ S7[x5] ^ S8[x4] ^ S6[x7];
    ks->K[14] = S5[xC] ^ S6[xD] ^ S7[x3] ^ S8[x2] ^ S7[x8];
    ks->K[15] = S5[xE] ^ S6[xF] ^ S7[x1] ^ S8[x0] ^ S8[xD];

    temp = S5[xD] ^ S6[xF] ^ S7[xC] ^ S8[xE] ^ S7[x8];
    z0 = x0 ^ BYTE0(temp);
    z1 = x1 ^ BYTE1(temp);
    z2 = x2 ^ BYTE2(temp);
    z3 = x3 ^ BYTE3(temp);

    temp = S5[z0] ^ S6[z2] ^ S7[z1] ^ S8[z3] ^ S8[xA];
    z4 = x8 ^ BYTE0(temp);
    z5 = x9 ^ BYTE1(temp);
    z6 = xA ^ BYTE2(temp);
    z7 = xB ^ BYTE3(temp);

    temp = S5[z7] ^ S6[z6] ^ S7[z5] ^ S8[z4] ^ S5[x9];
    z8 = xC ^ BYTE0(temp);
    z9 = xD ^ BYTE1(temp);
    zA = xE ^ BYTE2(temp);
    zB = xF ^ BYTE3(temp);

    temp = S5[zA] ^ S6[z9] ^ S7[zB] ^ S8[z8] ^ S6[xB];
    zC = x4 ^ BYTE0(temp);
    zD = x5 ^ BYTE1(temp);
    zE = x6 ^ BYTE2(temp);
    zF = x7 ^ BYTE3(temp);

    ks->K[16] = MASK(S5[z8] ^ S6[z9] ^ S7[z7] ^ S8[z6] ^ S5[z2]);
    ks->K[17] = MASK(S5[zA] ^ S6[zB] ^ S7[z5] ^ S8[z4] ^ S6[z6]);
    ks->K[18] = MASK(S5[zC] ^ S6[zD] ^ S7[z3] ^ S8[z2] ^ S7[z9]);
    ks->K[19] = MASK(S5[zE] ^ S6[zF] ^ S7[z1] ^ S8[z0] ^ S8[zC]);

    temp = S5[z5] ^ S6[z7] ^ S7[z4] ^ S8[z6] ^ S7[z0];
    x0 = z8 ^ BYTE0(temp);
    x1 = z9 ^ BYTE1(temp);
    x2 = zA ^ BYTE2(temp);
    x3 = zB ^ BYTE3(temp);

    temp = S5[x0] ^ S6[x2] ^ S7[x1] ^ S8[x3] ^ S8[z2];
    x4 = z0 ^ BYTE0(temp);
    x5 = z1 ^ BYTE1(temp);
    x6 = z2 ^ BYTE2(temp);
    x7 = z3 ^ BYTE3(temp);

    temp = S5[x7] ^ S6[x6] ^ S7[x5] ^ S8[x4] ^ S5[z1];
    x8 = z4 ^ BYTE0(temp);
    x9 = z5 ^ BYTE1(temp);
    xA = z6 ^ BYTE2(temp);
    xB = z7 ^ BYTE3(temp);

    temp = S5[xA] ^ S6[x9] ^ S7[xB] ^ S8[x8] ^ S6[z3];
    xC = zC ^ BYTE0(temp);
    xD = zD ^ BYTE1(temp);
    xE = zE ^ BYTE2(temp);
    xF = zF ^ BYTE3(temp);

    ks->K[20] = MASK(S5[x3] ^ S6[x2] ^ S7[xC] ^ S8[xD] ^ S5[x8]);
    ks->K[21] = MASK(S5[x1] ^ S6[x0] ^ S7[xE] ^ S8[xF] ^ S6[xD]);
    ks->K[22] = MASK(S5[x7] ^ S6[x6] ^ S7[x8] ^ S8[x9] ^ S7[x3]);
    ks->K[23] = MASK(S5[x5] ^ S6[x4] ^ S7[xA] ^ S8[xB] ^ S8[x7]);

    temp = S5[xD] ^ S6[xF] ^ S7[xC] ^ S8[xE] ^ S7[x8];
    z0 = x0 ^ BYTE0(temp);
    z1 = x1 ^ BYTE1(temp);
    z2 = x2 ^ BYTE2(temp);
    z3 = x3 ^ BYTE3(temp);

    temp = S5[z0] ^ S6[z2] ^ S7[z1] ^ S8[z3] ^ S8[xA];
    z4 = x8 ^ BYTE0(temp);
    z5 = x9 ^ BYTE1(temp);
    z6 = xA ^ BYTE2(temp);
    z7 = xB ^ BYTE3(temp);

    temp = S5[z7] ^ S6[z6] ^ S7[z5] ^ S8[z4] ^ S5[x9];
    z8 = xC ^ BYTE0(temp);
    z9 = xD ^ BYTE1(temp);
    zA = xE ^ BYTE2(temp);
    zB = xF ^ BYTE3(temp);

    temp = S5[zA] ^ S6[z9] ^ S7[zB] ^ S8[z8] ^ S6[xB];
    zC = x4 ^ BYTE0(temp);
    zD = x5 ^ BYTE1(temp);
    zE = x6 ^ BYTE2(temp);
    zF = x7 ^ BYTE3(temp);

    ks->K[24] = MASK(S5[z3] ^ S6[z2] ^ S7[zC] ^ S8[zD] ^ S5[z9]);
    ks->K[25] = MASK(S5[z1] ^ S6[z0] ^ S7[zE] ^ S8[zF] ^ S6[zC]);
    ks->K[26] = MASK(S5[z7] ^ S6[z6] ^ S7[z8] ^ S8[z9] ^ S7[z2]);
    ks->K[27] = MASK(S5[z5] ^ S6[z4] ^ S7[zA] ^ S8[zB] ^ S8[z6]);

    temp = S5[z5] ^ S6[z7] ^ S7[z4] ^ S8[z6] ^ S7[z0];
    x0 = z8 ^ BYTE0(temp);
    x1 = z9 ^ BYTE1(temp);
    x2 = zA ^ BYTE2(temp);
    x3 = zB ^ BYTE3(temp);

    temp = S5[x0] ^ S6[x2] ^ S7[x1] ^ S8[x3] ^ S8[z2];
    x4 = z0 ^ BYTE0(temp);
    x5 = z1 ^ BYTE1(temp);
    x6 = z2 ^ BYTE2(temp);
    x7 = z3 ^ BYTE3(temp);

    temp = S5[x7] ^ S6[x6] ^ S7[x5] ^ S8[x4] ^ S5[z1];
    x8 = z4 ^ BYTE0(temp);
    x9 = z5 ^ BYTE1(temp);
    xA = z6 ^ BYTE2(temp);
    xB = z7 ^ BYTE3(temp);

    temp = S5[xA] ^ S6[x9] ^ S7[xB] ^ S8[x8] ^ S6[z3];
    xC = zC ^ BYTE0(temp);
    xD = zD ^ BYTE1(temp);
    xE = zE ^ BYTE2(temp);
    xF = zF ^ BYTE3(temp);

    ks->K[28] = MASK(S5[x8] ^ S6[x9] ^ S7[x7] ^ S8[x6] ^ S5[x3]);
    ks->K[29] = MASK(S5[xA] ^ S6[xB] ^ S7[x5] ^ S8[x4] ^ S6[x7]);
    ks->K[30] = MASK(S5[xC] ^ S6[xD] ^ S7[x3] ^ S8[x2] ^ S7[x8]);
    ks->K[31] = MASK(S5[xE] ^ S6[xF] ^ S7[x1] ^ S8[x0] ^ S8[xD]);

    /* clear intermediate variables */
    memset(z,0,KEYSIZE);
    temp = 0;
}
#ifdef __BORLANDC__
#pragma warn .aus
#endif

/*
2.4.1. Masking Subkeys And Rotate Subkeys

   Let Km1, ..., Km16 be 32-bit masking subkeys (one per round).
   Let Kr1,    , Kr16 be 32-bit rotate subkeys (one per round); only the
   least significant 5 bits are used in each round.

   for (i=1; i<=16; i++)  { Kmi = Ki;  Kri = K16+i; }

2.2. Non-Identical Rounds

   Three different round functions are used in CAST-128.  The rounds are
   as follows (where "D" is the data input to the f function and "Ia" -
   "Id" are the most significant byte through least significant byte of
   I, respectively).  Note that "+" and "-" are addition and subtraction
   modulo 2**32, "^" is bitwise XOR, and "<<<" is the circular left-
   shift operation.

       Type 1:  I = ((Kmi + D) <<< Kri)
                f = ((S1[Ia] ^ S2[Ib]) - S3[Ic]) + S4[Id]

       Type 2:  I = ((Kmi ^ D) <<< Kri)
                f = ((S1[Ia] - S2[Ib]) + S3[Ic]) ^ S4[Id]

       Type 3:  I = ((Kmi - D) <<< Kri)
                f = ((S1[Ia] + S2[Ib]) ^ S3[Ic]) - S4[Id]

   Rounds 1, 4, 7, 10, 13, and 16 use f function Type 1.
   Rounds 2, 5, 8, 11, and 14 use f function Type 2.
   Rounds 3, 6, 9, 12, and 15 use f function Type 3.
*/

static uint32_t doRotL(uint32_t x, int r)
{
    return (x<<r)|(x>>(32-r));
}

static uint32_t f1(uint32_t D, uint32_t Kmi, uint32_t Kri)
{
    uint32_t I = doRotL((Kmi + D), (int)Kri);
    return ((S1[BYTE0(I)] ^ S2[BYTE1(I)]) - S3[BYTE2(I)]) + S4[BYTE3(I)];
}

static uint32_t f2(uint32_t D, uint32_t Kmi, uint32_t Kri)
{
    uint32_t I = doRotL((Kmi ^ D), (int)Kri);
    return ((S1[BYTE0(I)] - S2[BYTE1(I)]) + S3[BYTE2(I)]) ^ S4[BYTE3(I)];
}

static uint32_t f3(uint32_t D, uint32_t Kmi, uint32_t Kri)
{
    uint32_t I = doRotL((Kmi - D), (int)Kri);
    return ((S1[BYTE0(I)] + S2[BYTE1(I)]) ^ S3[BYTE2(I)]) - S4[BYTE3(I)];
}



static void cast_encrypt(uint32_t *Xl, uint32_t *Xr, pCAST5 ks)
{
    /*   2. (L0,R0) <-- (m1...m64).  (Split the plaintext into left and
              right 32-bit halves L0 = m1...m32 and R0 = m33...m64.) */
    uint32_t xl = *Xl, xr = *Xr, temp;

    /*   3. (16 rounds) for i from 1 to 16, compute Li and Ri as follows:
              Li = Ri-1;
              Ri = Li-1 ^ f(Ri-1,Kmi,Kri), where f is defined in Section 2.2
               (f is of Type 1, Type 2, or Type 3, depending on i).*/
    temp = xl;
    xl = xr;
    xr = temp ^ f1(xr, ks->K[0], ks->K[16]);

    temp = xl;
    xl = xr;
    xr = temp ^ f2(xr, ks->K[1], ks->K[17]);

    temp = xl;
    xl = xr;
    xr = temp ^ f3(xr, ks->K[2], ks->K[18]);

    temp = xl;
    xl = xr;
    xr = temp ^ f1(xr, ks->K[3], ks->K[19]);

    temp = xl;
    xl = xr;
    xr = temp ^ f2(xr, ks->K[4], ks->K[20]);

    temp = xl;
    xl = xr;
    xr = temp ^ f3(xr, ks->K[5], ks->K[21]);

    temp = xl;
    xl = xr;
    xr = temp ^ f1(xr, ks->K[6], ks->K[22]);

    temp = xl;
    xl = xr;
    xr = temp ^ f2(xr, ks->K[7], ks->K[23]);

    temp = xl;
    xl = xr;
    xr = temp ^ f3(xr, ks->K[8], ks->K[24]);

    temp = xl;
    xl = xr;
    xr = temp ^ f1(xr, ks->K[9], ks->K[25]);

    temp = xl;
    xl = xr;
    xr = temp ^ f2(xr, ks->K[10], ks->K[26]);

    temp = xl;
    xl = xr;
    xr = temp ^ f3(xr, ks->K[11], ks->K[27]);

    if(ks->rounds16)
    {
        temp = xl;
        xl = xr;
        xr = temp ^ f1(xr, ks->K[12], ks->K[28]);

        temp = xl;
        xl = xr;
        xr = temp ^ f2(xr, ks->K[13], ks->K[29]);

        temp = xl;
        xl = xr;
        xr = temp ^ f3(xr, ks->K[14], ks->K[30]);

        temp = xl;
        xl = xr;
        xr = temp ^ f1(xr, ks->K[15], ks->K[31]);
    }

    /*   4. c1...c64 <-- (R16,L16).  (Exchange final blocks L16, R16 and
              concatenate to form the ciphertext.)*/

    *Xl = xr;
    *Xr = xl;
}

/*
   Decryption is identical to the encryption algorithm given above,
   except that the rounds (and therefore the subkey pairs) are used in
   reverse order to compute (L0,R0) from (R16,L16).
*/

static void cast_decrypt(uint32_t *Xl, uint32_t *Xr, pCAST5 ks)
{
    /*   2. (L0,R0) <-- (m1...m64).  (Split the plaintext into left and
              right 32-bit halves L0 = m1...m32 and R0 = m33...m64.) */
    uint32_t xl = *Xr, xr = *Xl, temp;

    /*   3. (16 rounds) for i from 1 to 16, compute Li and Ri as follows:
              Li = Ri-1;
              Ri = Li-1 ^ f(Ri-1,Kmi,Kri), where f is defined in Section 2.2
               (f is of Type 1, Type 2, or Type 3, depending on i).
        
        so Ri-1 = Li
           Li-1 = Ri ^ f(Ri-1, Kmi, Kri) or Ri ^ f(Li, Kmi, Kri)
        
               */

    if(ks->rounds16)
    {
        temp = xr;
        xr = xl;
        xl = temp ^ f1(xr, ks->K[15], ks->K[31]);

        temp = xr;
        xr = xl;
        xl = temp ^ f3(xr, ks->K[14], ks->K[30]);

        temp = xr;
        xr = xl;
        xl = temp ^ f2(xr, ks->K[13], ks->K[29]);

        temp = xr;
        xr = xl;
        xl = temp ^ f1(xr, ks->K[12], ks->K[28]);
    }

    temp = xr;
    xr = xl;
    xl = temp ^ f3(xr, ks->K[11], ks->K[27]);

    temp = xr;
    xr = xl;
    xl = temp ^ f2(xr, ks->K[10], ks->K[26]);

    temp = xr;
    xr = xl;
    xl = temp ^ f1(xr, ks->K[9], ks->K[25]);

    temp = xr;
    xr = xl;
    xl = temp ^ f3(xr, ks->K[8], ks->K[24]);

    temp = xr;
    xr = xl;
    xl = temp ^ f2(xr, ks->K[7], ks->K[23]);

    temp = xr;
    xr = xl;
    xl = temp ^ f1(xr, ks->K[6], ks->K[22]);

    temp = xr;
    xr = xl;
    xl = temp ^ f3(xr, ks->K[5], ks->K[21]);

    temp = xr;
    xr = xl;
    xl = temp ^ f2(xr, ks->K[4], ks->K[20]);

    temp = xr;
    xr = xl;
    xl = temp ^ f1(xr, ks->K[3], ks->K[19]);

    temp = xr;
    xr = xl;
    xl = temp ^ f3(xr, ks->K[2], ks->K[18]);

    temp = xr;
    xr = xl;
    xl = temp ^ f2(xr, ks->K[1], ks->K[17]);

    temp = xr;
    xr = xl;
    xl = temp ^ f1(xr, ks->K[0], ks->K[16]);


    /*   4. c1...c64 <-- (R16,L16).  (Exchange final blocks L16, R16 and
              concatenate to form the ciphertext.)*/

    *Xr = xr;
    *Xl = xl;
}

/*
2.5. Variable Keysize

   The CAST-128 encryption algorithm has been designed to allow a key
   size that can vary from 40 bits to 128 bits, in 8-bit increments
   (that is, the allowable key sizes are 40, 48, 56, 64, ..., 112, 120,
   and 128 bits.  For variable keysize operation, the specification is
   as follows:

   1) For key sizes up to and including 80 bits (i.e., 40, 48, 56, 64,
      72, and 80 bits), the algorithm is exactly as specified but uses
      12 rounds instead of 16;

   2) For key sizes greater than 80 bits, the algorithm uses the full 16
      rounds;

   3) For key sizes less than 128 bits, the key is padded with zero
      bytes (in the rightmost, or least significant, positions) out to
      128 bits (since the CAST-128 key schedule assumes an input key of
      128 bits).

   Note that although CAST-128 can support all 12 key sizes listed
   above, 40 bits, 64 bits, 80 bits, and 128 bits are the sizes that
   find utility in typical environments.  Therefore, it will likely be
   sufficient for most implementations to support some subset of only
   these four sizes.

   In order to avoid confusion when variable keysize operation is used,
   the name CAST-128 is to be considered synonymous with the name CAST5;
   this allows a keysize to be appended without ambiguity.  Thus, for
   example, CAST-128 with a 40-bit key is to be referred to as CAST5-40;
   where a 128-bit key is explicitly intended, the name CAST5-128 should
   be used.
*/
static void initCAST5(uint8_t key[], short keylen, pCAST5 ks)
{
    uint8_t x[KEYSIZE];

    /* expand the key */
    memset(x, 0, KEYSIZE);
    memcpy(x, key, keylen);

    /* generate key schedule */
    generateKeySchedule(x, ks);
    memset(x, 0, KEYSIZE);

    /* set round count */
    ks->rounds16 = (boolean)(keylen > 10);
}

/************************************************************************/
/* generic routines */
static void initCAST5_NN(byte *key, long keylen, int triple,
void **keysched, size_t *length)
{
    int keys = triple ? 3 : 1;
    int i;
    pCAST5 b;

    *length = sizeof(CAST5)*keys;
    *keysched = zmalloc(*length);
    if(!(*keysched)) return;

    b = (pCAST5) *keysched;

    for(i=0; i < keys; i++)
    {
        initCAST5(key, (short)keylen, b+i );
        key+=(size_t)keylen;
    }

}

static void ecbCAST5_NN(void *keysched, int triple, int encrypt, byte *in, byte *out)
{
    int i, keys = triple ? 3 : 1;
    uint32_t text[2];
    pCAST5 b = (pCAST5)keysched;

    if(triple & !encrypt) b+=2;

    /* pack byte streams in MSB-first form into the 32bit integers */
    /* so that the MSB becomes the high byte, regardless of architecture */
    text[0] = ((uint32_t)in[0]<<24)+((uint32_t)in[1]<<16)
        +((uint32_t)in[2]<<8)+in[3];
    text[1] = ((uint32_t)in[4]<<24)+((uint32_t)in[5]<<16)
        +((uint32_t)in[6]<<8)+in[7];

    for(i=0; i<keys; i++)
    {
        if(encrypt) cast_encrypt(text, text+1, b+i );
        else cast_decrypt(text, text+1, b-i );
    }

    /* and unpack back into MSB-first format*/
    out[0] = (byte)((text[0]>>24) & 0xFF); 
    out[1] = (byte)((text[0]>>16) & 0xFF);
    out[2] = (byte)((text[0]>> 8) & 0xFF); 
    out[3] = (byte)( text[0] & 0xFF);
    out[4] = (byte)((text[1]>>24) & 0xFF); 
    out[5] = (byte)((text[1]>>16) & 0xFF);
    out[6] = (byte)((text[1]>> 8) & 0xFF); 
    out[7] = (byte)( text[1] & 0xFF);
}

static void destroyCAST5_NN(void **keysched, size_t length)
{
    /* match memory deallocation to allocation locally! */
    if(length>0) zfree(keysched, length);
}

/************************************************************************/
/* standard routines */
void initCAST5_128(byte *key, int triple, void **keysched, size_t *length)
{
    initCAST5_NN(key, 16L, triple, keysched, length);
}

void ecbCAST5_128(void *keysched, int triple, int encrypt, byte *in, byte *out)
{
    ecbCAST5_NN(keysched, triple, encrypt, in, out);
}

void destroyCAST5_128(void **keysched, size_t length)
{
    destroyCAST5_NN(keysched, length);
}
/************************************************************************/

#ifdef TEST

void * zmalloc(size_t n)
{
    void * result = malloc(n);

    if(result) memset(result, 0, n);
    return result;
}

void zfree (void ** buffer, size_t n)
{
    memset(*buffer, 0, n);
    free(*buffer);
    *buffer = 0;
}


int main()
{
    uint8_t key[16] = {
        0x01, 0x23, 0x45, 0x67, 0x12, 0x34, 0x56, 0x78,
        0x23, 0x45, 0x67, 0x89, 0x34, 0x56, 0x78, 0x9A     };
    uint8_t plain[8]= {
        0x01, 0x23, 0x45, 0x67, 0x89, 0xAB, 0xCD, 0xEF     };
    uint8_t in[8], out[8];
    CAST5 k;
    /*
        Appendix B. Test Vectors
        
           This appendix provides test vectors for the CAST-128 cipher described
           this document.
        
        B.1. Single Plaintext-Key-Ciphertext Sets
        
           In order to ensure that the algorithm is implemented correctly, the
           following test vectors can be used for verification (values given in
           hexadecimal notation).
        
           128-bit key         = 01 23 45 67 12 34 56 78 23 45 67 89 34 56 78 9A
                   plaintext   = 01 23 45 67 89 AB CD EF
                   ciphertext  = 23 8B 4F E5 84 7E 44 B2
        
           80-bit  key         = 01 23 45 67 12 34 56 78 23 45
                               = 01 23 45 67 12 34 56 78 23 45 00 00 00 00 00 00
                   plaintext   = 01 23 45 67 89 AB CD EF
                   ciphertext  = EB 6A 71 1A 2C 02 27 1B
        
           40-bit  key         = 01 23 45 67 12
                               = 01 23 45 67 12 00 00 00 00 00 00 00 00 00 00 00
                   plaintext   = 01 23 45 67 89 AB CD EF
                   ciphertext  = 7A C8 16 D1 6E 9B 30 2E
        */

    memcpy(in, plain, 8);
    initCAST5(key, (short)16, &k);
    ecbCAST5_NN(&k, 0, 1, in, out);
    printf("Expect Cypher: 23 8B 4F E5 84 7E 44 B2\n");
    printf("Get    Cypher: %02x %02x %02x %02x %02x %02x %02x %02x\n",
    out[0],out[1],out[2],out[3],out[4],out[5],out[6],out[7]);
    ecbCAST5_NN(&k, 0, 0, out, in);
    printf("Expect Plain : 01 23 45 67 89 AB CD EF\n");
    printf("Get    Plain : %02x %02x %02x %02x %02x %02x %02x %02x\n\n",
    in[0],in[1],in[2],in[3],in[4],in[5],in[6],in[7]);

    initCAST5(key, (short)10, &k);
    ecbCAST5_NN(&k, 0, 1, in, out);
    printf("Expect Cypher: EB 6A 71 1A 2C 02 27 1B\n");
    printf("Get    Cypher: %02x %02x %02x %02x %02x %02x %02x %02x\n",
    out[0],out[1],out[2],out[3],out[4],out[5],out[6],out[7]);
    ecbCAST5_NN(&k, 0, 0, out, in);
    printf("Expect Plain : 01 23 45 67 89 AB CD EF\n");
    printf("Get    Plain : %02x %02x %02x %02x %02x %02x %02x %02x\n\n",
    in[0],in[1],in[2],in[3],in[4],in[5],in[6],in[7]);


    initCAST5(key, (short)5, &k);
    ecbCAST5_NN(&k, 0, 1, in, out);
    printf("Expect Cypher: 7A C8 16 D1 6E 9B 30 2E\n");
    printf("Get    Cypher: %02x %02x %02x %02x %02x %02x %02x %02x\n",
    out[0],out[1],out[2],out[3],out[4],out[5],out[6],out[7]);
    ecbCAST5_NN(&k, 0, 0, out, in);
    printf("Expect Plain : 01 23 45 67 89 AB CD EF\n");
    printf("Get    Plain : %02x %02x %02x %02x %02x %02x %02x %02x\n\n",
    in[0],in[1],in[2],in[3],in[4],in[5],in[6],in[7]);

    /*
        B.2. Full Maintenance Test
        
           A maintenance test for CAST-128 has been defined to verify the
           correctness of implementations.  It is defined in pseudo-code as
           follows, where a and b are 128-bit vectors, aL and aR are the
           leftmost and rightmost halves of a, bL and bR are the leftmost and
           rightmost halves of b, and encrypt(d,k) is the encryption in ECB mode
           of block d under key k.
        
           Initial a = 01 23 45 67 12 34 56 78 23 45 67 89 34 56 78 9A (hex)
           Initial b = 01 23 45 67 12 34 56 78 23 45 67 89 34 56 78 9A (hex)
        
           do 1,000,000 times
           {
               aL = encrypt(aL,b)
               aR = encrypt(aR,b)
               bL = encrypt(bL,a)
               bR = encrypt(bR,a)
           }
        
        Verify a == EE A9 D0 A2 49 FD 3B A6 B3 43 6F B8 9D 6D CA 92 (hex)
        Verify b == B2 C9 5E B0 0C 31 AD 71 80 AC 05 B8 E8 3D 69 6E (hex)
        
        */
    {
        uint8_t a[16]= {
            0x01, 0x23, 0x45, 0x67, 0x12, 0x34, 0x56, 0x78,
            0x23, 0x45, 0x67, 0x89, 0x34, 0x56, 0x78, 0x9A         };
        uint8_t b[16]= {
            0x01, 0x23, 0x45, 0x67, 0x12, 0x34, 0x56, 0x78,
            0x23, 0x45, 0x67, 0x89, 0x34, 0x56, 0x78, 0x9A         };

        uint32_t i;

        for(i=0; i<100; ++i)
        {
#ifndef TEST2
            int j;
            printf("%d%% complete\r", i);
            for(j=0; j<10000; ++j)
            {
                initCAST5(b, 16, &k);
                ecbCAST5_NN(&k, 0, 1, a, a);
                ecbCAST5_NN(&k, 0, 1, a+8, a+8);
                initCAST5(a, 16, &k);
                ecbCAST5_NN(&k, 0, 1, b, b);
                ecbCAST5_NN(&k, 0, 1, b+8, b+8);
            }
#endif
        }

        printf("Expect a == EE A9 D0 A2 49 FD 3B A6 B3 43 6F B8 9D 6D CA 92\n");
        printf("Verify a ==");
        for(i=0; i<16;++i) printf(" %02x", a[i]);
        printf("\n");
        printf("Expect b == B2 C9 5E B0 0C 31 AD 71 80 AC 05 B8 E8 3D 69 6E\n");
        printf("Verify b ==");
        for(i=0; i<16;++i) printf(" %02x", b[i]);
        printf("\n");


    }



    return 0;
}
#endif
/* end of file cast5.c */
